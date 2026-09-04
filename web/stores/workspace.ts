import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export type PanelId =
  | 'editor'
  | 'terminal'
  | 'files'
  | 'ai'
  | 'browser'
  | 'monitor'
  | 'projects'
  | 'settings'
  | 'agent'
  | 'computer';

export type PanelPosition = {
  x: number;
  y: number;
  w: number;
  h: number;
  z: number;
  minimized: boolean;
  maximized: boolean;
};

export type PanelState = {
  id: PanelId;
  title: string;
  icon: string;
  position: PanelPosition;
  visible: boolean;
  focused: boolean;
};

type WorkspaceState = {
  panels: Record<PanelId, PanelState>;
  activePanel: PanelId | null;
  layoutMode: 'float' | 'grid';
  focusPanel: (id: PanelId) => void;
  togglePanel: (id: PanelId) => void;
  hidePanel: (id: PanelId) => void;
  showPanel: (id: PanelId) => void;
  movePanel: (id: PanelId, x: number, y: number) => void;
  resizePanel: (id: PanelId, w: number, h: number) => void;
  setZ: (id: PanelId, z: number) => void;
  minimizePanel: (id: PanelId) => void;
  maximizePanel: (id: PanelId) => void;
  restorePanels: () => void;
  setLayoutMode: (mode: 'float' | 'grid') => void;
};

const DEFAULT_SIZE = { w: 560, h: 420 };
const PANEL_META: Record<PanelId, { title: string; icon: string; pos: { x: number; y: number } }> = {
  editor: { title: 'Code Editor', icon: 'Code2', pos: { x: 60, y: 80 } },
  terminal: { title: 'Terminal', icon: 'TerminalSquare', pos: { x: 620, y: 520 } },
  files: { title: 'Files', icon: 'FolderTree', pos: { x: 20, y: 40 } },
  ai: { title: 'AI Assistant', icon: 'Sparkles', pos: { x: 380, y: 100 } },
  agent: { title: 'Virgo Agent', icon: 'Bot', pos: { x: 320, y: 250 } },
  computer: { title: 'VirgoYT Computer', icon: 'Monitor', pos: { x: 160, y: 90 } },
  browser: { title: 'Browser Sandbox', icon: 'Globe', pos: { x: 120, y: 140 } },
  monitor: { title: 'System Monitor', icon: 'Activity', pos: { x: 700, y: 200 } },
  projects: { title: 'Projects', icon: 'FolderKanban', pos: { x: 200, y: 400 } },
  settings: { title: 'Settings', icon: 'Settings', pos: { x: 720, y: 380 } },
};

function initialState(): Record<PanelId, PanelState> {
  const panels = {} as Record<PanelId, PanelState>;
  (Object.keys(PANEL_META) as PanelId[]).forEach((id, i) => {
    const meta = PANEL_META[id];
    const visible = id === 'editor' || id === 'terminal' || id === 'files' || id === 'agent';
    panels[id] = {
      id,
      title: meta.title,
      icon: meta.icon,
      position: {
        x: meta.pos.x + (i % 3) * 20,
        y: meta.pos.y + (i % 2) * 20,
        w: DEFAULT_SIZE.w,
        h: DEFAULT_SIZE.h,
        z: i + 1,
        minimized: false,
        maximized: false,
      },
      visible,
      focused: id === 'editor',
    };
  });
  return panels;
}

let zCounter = 10;

export const useWorkspaceStore = create<WorkspaceState>()(
  persist(
    (set) => ({
      panels: initialState(),
      activePanel: 'editor',
      layoutMode: 'float',

      focusPanel: (id) =>
        set((s) => {
          const nextZ = ++zCounter;
          const panels = { ...s.panels };
          Object.keys(panels).forEach((k) => {
            panels[k as PanelId] = { ...panels[k as PanelId], focused: false };
          });
          panels[id] = {
            ...panels[id],
            focused: true,
            position: { ...panels[id].position, z: nextZ },
          };
          return { panels, activePanel: id };
        }),

      togglePanel: (id) =>
        set((s) => ({
          panels: {
            ...s.panels,
            [id]: {
              ...s.panels[id],
              visible: !s.panels[id].visible,
            },
          },
        })),

      hidePanel: (id) =>
        set((s) => ({
          panels: { ...s.panels, [id]: { ...s.panels[id], visible: false } },
        })),

      showPanel: (id) =>
        set((s) => {
          const nextZ = ++zCounter;
          return {
            panels: {
              ...s.panels,
              [id]: { ...s.panels[id], visible: true, position: { ...s.panels[id].position, z: nextZ } },
            },
            activePanel: id,
          };
        }),

      movePanel: (id, x, y) =>
        set((s) => ({
          panels: {
            ...s.panels,
            [id]: { ...s.panels[id], position: { ...s.panels[id].position, x, y } },
          },
        })),

      resizePanel: (id, w, h) =>
        set((s) => ({
          panels: {
            ...s.panels,
            [id]: { ...s.panels[id], position: { ...s.panels[id].position, w, h } },
          },
        })),

      setZ: (id, z) =>
        set((s) => ({
          panels: { ...s.panels, [id]: { ...s.panels[id], position: { ...s.panels[id].position, z } } },
        })),

      minimizePanel: (id) =>
        set((s) => ({
          panels: {
            ...s.panels,
            [id]: { ...s.panels[id], position: { ...s.panels[id].position, minimized: true } },
          },
        })),

      maximizePanel: (id) =>
        set((s) => ({
          panels: {
            ...s.panels,
            [id]: { ...s.panels[id], position: { ...s.panels[id].position, maximized: !s.panels[id].position.maximized } },
          },
        })),

      restorePanels: () => set({ panels: initialState(), activePanel: 'editor' }),

      setLayoutMode: (mode) => set({ layoutMode: mode }),
    }),
    {
      name: 'virgo-workspace',
      partialize: (s) => ({
        layoutMode: s.layoutMode,
      }),
    }
  )
);
