import { create } from 'zustand';

export type SystemMetrics = {
  cpu: number;
  memUsed: number;
  memTotal: number;
  diskUsed: number;
  diskTotal: number;
  network: { up: number; down: number };
  uptime: number;
  processes: { pid: number; name: string; cpu: number; mem: number }[];
  os: string;
  kernel: string;
  hostname: string;
};

type SystemState = {
  connected: boolean;
  sessionId: string | null;
  sandboxReady: boolean;
  metrics: SystemMetrics | null;
  authToken: string | null;
  user: { name: string; email: string } | null;
  setConnected: (v: boolean) => void;
  setSessionId: (id: string) => void;
  setSandboxReady: (v: boolean) => void;
  setMetrics: (m: SystemMetrics) => void;
  setAuthToken: (t: string | null) => void;
  setUser: (u: { name: string; email: string } | null) => void;
};

export const useSystemStore = create<SystemState>((set) => ({
  connected: false,
  sessionId: null,
  sandboxReady: false,
  metrics: null,
  authToken: null,
  user: null,
  setConnected: (connected) => set({ connected }),
  setSessionId: (sessionId) => set({ sessionId }),
  setSandboxReady: (sandboxReady) => set({ sandboxReady }),
  setMetrics: (metrics) => set({ metrics }),
  setAuthToken: (authToken) => set({ authToken }),
  setUser: (user) => set({ user }),
}));
