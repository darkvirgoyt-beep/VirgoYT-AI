import { create } from 'zustand';

export type TerminalTab = {
  id: string;
  name: string;
  color: string;
};

export type TerminalLine = {
  id: string;
  type: 'input' | 'output' | 'system' | 'error' | 'success';
  text: string;
  timestamp: number;
};

type TerminalState = {
  tabs: TerminalTab[];
  activeTabId: string;
  connected: boolean;
  lines: Record<string, TerminalLine[]>;
  addTab: () => void;
  removeTab: (id: string) => void;
  setActiveTab: (id: string) => void;
  setConnected: (connected: boolean) => void;
  writeLine: (tabId: string, type: TerminalLine['type'], text: string) => void;
  clearTab: (tabId: string) => void;
  clearAll: () => void;
};

let tabCounter = 1;
const TAB_COLORS = ['#00ff9c', '#00d4ff', '#b967ff', '#ffb547', '#ff5c7a'];

const makeTab = (): TerminalTab => {
  const id = `term-${Date.now()}-${tabCounter}`;
  const name = `Terminal ${tabCounter}`;
  tabCounter += 1;
  return {
    id,
    name,
    color: TAB_COLORS[(tabCounter - 2) % TAB_COLORS.length],
  };
};

export const useTerminalStore = create<TerminalState>((set, get) => {
  const first = makeTab();
  return {
    tabs: [first],
    activeTabId: first.id,
    connected: false,
    lines: { [first.id]: [] },

    addTab: () =>
      set((s) => {
        const tab = makeTab();
        return {
          tabs: [...s.tabs, tab],
          activeTabId: tab.id,
          lines: { ...s.lines, [tab.id]: [] },
        };
      }),

    removeTab: (id) =>
      set((s) => {
        if (s.tabs.length <= 1) return s;
        const tabs = s.tabs.filter((t) => t.id !== id);
        const lines = { ...s.lines };
        delete lines[id];
        const activeTabId = s.activeTabId === id ? tabs[tabs.length - 1].id : s.activeTabId;
        return { tabs, lines, activeTabId };
      }),

    setActiveTab: (activeTabId) => set({ activeTabId }),

    setConnected: (connected) => set({ connected }),

    writeLine: (tabId, type, text) =>
      set((s) => ({
        lines: {
          ...s.lines,
          [tabId]: [
            ...(s.lines[tabId] ?? []),
            { id: `${Date.now()}-${Math.random()}`, type, text, timestamp: Date.now() },
          ],
        },
      })),

    clearTab: (tabId) =>
      set((s) => ({
        lines: { ...s.lines, [tabId]: [] },
      })),

    clearAll: () =>
      set(() => {
        const lines: Record<string, TerminalLine[]> = {};
        get().tabs.forEach((t) => (lines[t.id] = []));
        return { lines };
      }),
  };
});
