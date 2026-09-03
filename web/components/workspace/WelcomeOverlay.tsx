'use client';

import { motion } from 'framer-motion';
import { useWorkspaceStore } from '@/stores/workspace';
import { APP_NAME } from '@/lib/constants';
import { Sparkles, Code2, TerminalSquare, Globe, MonitorSmartphone } from 'lucide-react';

export default function WelcomeOverlay({ onEnter }: { onEnter: () => void }) {
  const showPanel = useWorkspaceStore((s) => s.showPanel);

  const quickActions = [
    { icon: Sparkles, label: 'Ask AI to build', action: () => showPanel('ai'), color: '#b967ff' },
    { icon: Code2, label: 'Open code editor', action: () => showPanel('editor'), color: '#3375ff' },
    { icon: TerminalSquare, label: 'Launch terminal', action: () => showPanel('terminal'), color: '#00ff9c' },
    { icon: Globe, label: 'Browse web', action: () => showPanel('browser'), color: '#00d4ff' },
  ];

  return (
    <motion.div
      className="absolute inset-0 z-[2000] flex items-center justify-center bg-void-950/70 backdrop-blur-md"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.4 }}
    >
      <motion.div
        className="max-w-lg w-[90%] text-center"
        initial={{ scale: 0.9, y: 20, opacity: 0 }}
        animate={{ scale: 1, y: 0, opacity: 1 }}
        exit={{ scale: 0.95, opacity: 0 }}
        transition={{ delay: 0.1, type: 'spring', stiffness: 200, damping: 20 }}
      >
        <motion.div
          className="mx-auto mb-6 relative h-20 w-20"
          animate={{ rotate: 360 }}
          transition={{ duration: 20, repeat: Infinity, ease: 'linear' }}
        >
          <div className="absolute inset-0 rounded-2xl bg-gradient-to-br from-virgo-500/40 to-terminal-purple/40 blur-xl" />
          <div className="absolute inset-0 rounded-2xl border-2 border-virgo-400/60 flex items-center justify-center">
            <span className="text-2xl font-bold text-white">V</span>
          </div>
        </motion.div>

        <h1 className="text-4xl font-bold mb-2 accent-gradient-text">{APP_NAME}</h1>
        <p className="text-gray-400 mb-8 text-sm leading-relaxed">
          Your autonomous cloud AI computer.
          <br />
          Write code, run a real Linux terminal, browse the web, and build full apps —
          all from one holographic 3D workspace.
        </p>

        <div className="flex flex-col gap-3 mb-8 max-w-xs mx-auto">
          {quickActions.map((action, i) => (
            <motion.button
              key={action.label}
              onClick={() => {
                action.action();
                onEnter();
              }}
              className="glass-panel-hover flex items-center gap-3 px-4 py-3 text-left group"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.3 + i * 0.08 }}
              whileHover={{ x: 4 }}
              whileTap={{ scale: 0.98 }}
            >
              <action.icon size={18} style={{ color: action.color }} />
              <span className="text-sm text-gray-200 group-hover:text-white">{action.label}</span>
              <span className="ml-auto text-gray-500 text-xs group-hover:text-gray-300">→</span>
            </motion.button>
          ))}
        </div>

        <motion.button
          onClick={onEnter}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.97 }}
          className="px-8 py-3 rounded-xl bg-gradient-to-r from-virgo-600 to-terminal-purple text-white font-semibold text-sm shadow-lg shadow-virgo-600/30 hover:shadow-virgo-500/40 transition-shadow"
        >
          Enter Workspace
        </motion.button>
      </motion.div>
    </motion.div>
  );
}
