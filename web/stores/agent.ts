import { create } from 'zustand';
import { io, type Socket } from 'socket.io-client';

export type AgentEvent =
  | { type: 'run-start'; goal: string; id: string }
  | { type: 'thought'; id: string; text: string }
  | { type: 'plan'; id: string; steps: string[] }
  | { type: 'tool-start'; id: string; tool: string; input: string }
  | { type: 'tool-end'; id: string; tool: string; output: string }
  | { type: 'command'; id: string; command: string; output: string }
  | { type: 'browse'; id: string; url: string; title?: string }
  | { type: 'site-preview'; id: string; url: string }
  | { type: 'download'; id: string; url: string; path: string; size: number }
  | { type: 'file-write'; id: string; path: string }
  | { type: 'confirmation-required'; id: string; action: string; reason: string }
  | { type: 'confirmation-denied'; id: string; reason: string }
  | { type: 'result'; id: string; summary: string }
  | { type: 'error'; id: string; message: string }
  | { type: 'run-end'; id: string };

export type WorkforceEvent =
  | { type: 'wf-agent-start'; agent: string; task: string }
  | { type: 'wf-agent-done'; agent: string; output: string }
  | { type: 'wf-plan'; steps: string[] }
  | { type: 'wf-result'; summary: string }
  | { type: 'wf-error'; message: string };

export type FactoryEvent =
  | { type: 'factory-stage'; stage: string; message: string }
  | { type: 'factory-plan'; steps: string[] }
  | { type: 'factory-command'; command: string; output: string }
  | { type: 'factory-file'; path: string }
  | { type: 'factory-result'; summary: string }
  | { type: 'factory-error'; message: string };

export type ScanFinding = {
  severity: 'critical' | 'high' | 'medium' | 'low' | 'info';
  category: 'secret' | 'dependency' | 'config' | 'hardening';
  title: string;
  detail: string;
  file?: string;
  line?: number;
};

export type ScanReport = {
  scanned: number;
  findings: ScanFinding[];
  summary: string;
  generatedAt: number;
};

export type ConnectorState = { id: string; name: string; configured: boolean; authorized: boolean; scopes: string; authUrl?: string };
export type ExportTarget = 'web' | 'exe' | 'apk' | 'mac' | 'terminal';
export type RunbookKind = { id: string; label: string };

type AgentState = {
  events: AgentEvent[];
  wfEvents: WorkforceEvent[];
  fxEvents: FactoryEvent[];
  roster: { id: string; name: string; emoji: string; role: string }[];
  running: boolean;
  socket: Socket | null;
  previewUrl: string | null;
  pendingConfirm: { action: string; reason: string } | null;
  scan: ScanReport | null;
  connectors: ConnectorState[];
  labs: { id: string; title: string }[];
  runbookKinds: RunbookKind[];
  init: (sessionId: string) => void;
  run: (goal: string, sessionId: string, model?: string) => Promise<void>;
  confirm: (approve: boolean) => void;
  setPreview: (url: string | null) => void;
  clear: () => void;
  loadRoster: () => Promise<void>;
  runWorkforce: (goal: string, sessionId: string, agents?: string[]) => Promise<void>;
  runFactory: (idea: string, sessionId: string) => Promise<void>;
  runScan: (sessionId: string) => Promise<void>;
  exportProject: (sessionId: string, target: ExportTarget, appName?: string) => Promise<string | null>;
  loadConnectors: () => Promise<void>;
  loadLabs: () => Promise<void>;
  loadRunbookKinds: () => Promise<void>;
  runbook: (kind: string, context: string, owner?: string) => Promise<string | null>;
};

const API = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

export const useAgentStore = create<AgentState>((set, get) => ({
  events: [],
  wfEvents: [],
  fxEvents: [],
  roster: [],
  running: false,
  socket: null,
  previewUrl: null,
  pendingConfirm: null,
  scan: null,
  connectors: [],
  labs: [],
  runbookKinds: [],

  init: (sessionId) => {
    if (get().socket) return;
    const socket = io(API, {
      transports: ['websocket'],
      query: { sessionId },
    });
    socket.emit('agent:watch', { sessionId });
    socket.on('agent:event', (e: AgentEvent) => {
      set((s) => ({ events: [...s.events, e] }));
      if (e.type === 'confirmation-required') {
        set({ pendingConfirm: { action: e.action, reason: e.reason } });
      } else if (e.type === 'confirmation-denied') {
        set({ pendingConfirm: null });
      } else if (e.type === 'run-end') {
        set({ running: false });
      } else if (e.type === 'site-preview' || e.type === 'browse') {
        set({ previewUrl: e.url });
      }
    });
    socket.on('workforce:event', (e: WorkforceEvent) => set((s) => ({ wfEvents: [...s.wfEvents, e] })));
    socket.on('factory:event', (e: FactoryEvent) => set((s) => ({ fxEvents: [...s.fxEvents, e] })));
    set({ socket });
  },

  run: async (goal, sessionId, model = 'auto') => {
    set({ running: true, events: [], pendingConfirm: null });
    try {
      const res = await fetch(`${API}/api/agent/run`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ goal, sessionId, model }),
      });
      const data = await res.json();
      if (!data.ok && data.error) {
        set((s) => ({ running: false, events: [...s.events, { type: 'error', id: 'x', message: data.error }] }));
      }
    } catch (e: any) {
      set((s) => ({ running: false, events: [...s.events, { type: 'error', id: 'x', message: e.message }] }));
    }
  },

  confirm: (approve) => {
    const socket = get().socket;
    if (socket) socket.emit('agent:confirm', { sessionId: (socket as any).handshake?.auth?.sessionId, approve });
    set({ pendingConfirm: null });
  },

  setPreview: (url) => set({ previewUrl: url }),
  clear: () => set({ events: [], wfEvents: [], fxEvents: [], previewUrl: null, pendingConfirm: null }),

  loadRoster: async () => {
    try {
      const res = await fetch(`${API}/api/agent/roster`);
      const data = await res.json();
      set({ roster: data.agents ?? [] });
    } catch {}
  },

  runWorkforce: async (goal, sessionId, agents) => {
    set({ wfEvents: [] });
    try {
      await fetch(`${API}/api/agent/workforce`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ goal, sessionId, agents }),
      });
    } catch {}
  },

  runFactory: async (idea, sessionId) => {
    set({ fxEvents: [] });
    try {
      await fetch(`${API}/api/agent/factory`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idea, sessionId }),
      });
    } catch {}
  },

  runScan: async (sessionId) => {
    set({ scan: null });
    try {
      const res = await fetch(`${API}/api/cyber/scan`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId }),
      });
      if (!res.ok) return;
      set({ scan: await res.json() });
    } catch {}
  },

  exportProject: async (sessionId, target, appName) => {
    try {
      const res = await fetch(`${API}/api/build/export`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId, target, appName }),
      });
      if (!res.ok) return null;
      const data = await res.json();
      return data.archivePath ?? null;
    } catch {
      return null;
    }
  },

  loadConnectors: async () => {
    try {
      const res = await fetch(`${API}/api/build/connectors`);
      const data = await res.json();
      set({ connectors: data.connectors ?? [] });
    } catch {}
  },

  loadLabs: async () => {
    try {
      const res = await fetch(`${API}/api/cyber/labs`);
      const data = await res.json();
      set({ labs: data.labs ?? [] });
    } catch {}
  },

  loadRunbookKinds: async () => {
    try {
      const res = await fetch(`${API}/api/cyber/runbook/kinds`);
      const data = await res.json();
      set({ runbookKinds: data.kinds ?? [] });
    } catch {}
  },

  runbook: async (kind, context, owner) => {
    try {
      const res = await fetch(`${API}/api/cyber/runbook`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ kind, context, owner }),
      });
      if (!res.ok) return null;
      const data = await res.json();
      return data.markdown ?? null;
    } catch {
      return null;
    }
  },
}));