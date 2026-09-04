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

type AgentState = {
  events: AgentEvent[];
  running: boolean;
  socket: Socket | null;
  previewUrl: string | null;
  pendingConfirm: { action: string; reason: string } | null;
  init: (sessionId: string) => void;
  run: (goal: string, sessionId: string, model?: string) => Promise<void>;
  confirm: (approve: boolean) => void;
  setPreview: (url: string | null) => void;
  clear: () => void;
};

const API = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

export const useAgentStore = create<AgentState>((set, get) => ({
  events: [],
  running: false,
  socket: null,
  previewUrl: null,
  pendingConfirm: null,

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
  clear: () => set({ events: [], previewUrl: null, pendingConfirm: null }),
}));