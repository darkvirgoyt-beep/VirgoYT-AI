'use client';

import * as Icons from 'lucide-react';
import { motion } from 'framer-motion';
import { useWorkspaceStore, PanelId } from '@/stores/workspace';
import { useSystemStore } from '@/stores/system';

const DOCK_ITEMS: { id: PanelId; icon: string; label: string; color: string }[] = [
  { id: 'editor', icon: 'Code2', label: 'Editor', color: '#3375ff' },
  { id: 'terminal', icon: 'TerminalSquare', label: 'Terminal', color: '#00ff9c' },
  { id: 'files', icon: 'FolderTree', label: 'Files', color: '#ffb547' },
  { id: 'ai', icon: 'Sparkles', label: 'AI', color: '#b967ff' },
  { id: 'browser', icon: 'Globe', label: 'Browser', color: '#00d4ff' },
  { id: 'monitor', icon: 'Activity', label: 'Monitor', color: '#ff5c7a' },
];

export function Taskbar() {
  const panels = useWorkspaceStore((s) => s.panels);
  const togglePanel = useWorkspaceStore((s) => s.togglePanel);
  const showPanel = useWorkspaceStore((s) => s.showPanel);
  const connected = useSystemStore((s) => s.connected);

  return (
    <div className="absolute bottom-3 left-1/2 -translate-x-1/2 z-[9999]">
      <motion.div
        className="glass-panel flex items-center gap-1.5 px-2 py-1.5"
        initial={{ y: 60, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ delay: 0.4, type: 'spring', stiffness: 200, damping: 20 }}
      >
        {DOCK_ITEMS.map((item) => {
          const panel = panels[item.id];
          const Icon = (Icons as Record<string, any>)[item.icon] ?? Icons.Box;
          const active = panel?.visible ?? false;
          return (
            <button
              key={item.id}
              onClick={() => (active ? togglePanel(item.id) : showPanel(item.id))}
              className="relative p-2 rounded-xl hover:bg-white/10 transition-colors group"
              aria-label={item.label}
            >
              <Icon size={20} style={{ color: item.color }} />
              {active && (
                <span
                  className="absolute bottom-0.5 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full"
                  style={{ background: item.color, boxShadow: `0 0 6px ${item.color}` }}
                />
              )}
              <span className="absolute -top-8 left-1/2 -translate-x-1/2 text-[10px] px-2 py-0.5 rounded-md bg-void-900 border border-white/10 text-gray-300 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none whitespace-nowrap">
                {item.label}
              </span>
            </button>
          );
        })}

        <div className="w-px h-6 bg-white/10 mx-1" />

        <div className="flex items-center gap-1.5 px-2">
          <span
            className={`h-1.5 w-1.5 rounded-full animate-pulse ${
              connected ? 'bg-terminal-green' : 'bg-terminal-amber'
            }`}
          />
          <span className="text-[9px] text-gray-500 uppercase tracking-wider">
            {connected ? 'online' : 'offline'}
          </span>
        </div>
      </motion.div>
    </div>
  );
}
