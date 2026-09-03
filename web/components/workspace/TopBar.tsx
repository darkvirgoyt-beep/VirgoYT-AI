'use client';

import { motion } from 'framer-motion';
import { useSystemStore } from '@/stores/system';
import { APP_NAME } from '@/lib/constants';
import { useWorkspaceStore } from '@/stores/workspace';

export function TopBar() {
  const connected = useSystemStore((s) => s.connected);
  const sessionId = useSystemStore((s) => s.sessionId);

  return (
    <motion.div
      className="absolute top-0 left-0 right-0 z-[50] flex items-center gap-4 px-4 py-2"
      initial={{ y: -40, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ delay: 0.2, duration: 0.4 }}
    >
      <div className="flex items-center gap-2">
        <div className="relative h-7 w-7">
          <div className="absolute inset-0 rounded-lg bg-virgo-600 opacity-30 blur-md" />
          <div className="relative h-7 w-7 rounded-lg bg-gradient-to-br from-virgo-500 to-terminal-purple flex items-center justify-center">
            <span className="text-white text-xs font-bold">V</span>
          </div>
        </div>
        <span className="text-sm font-semibold accent-gradient-text tracking-wide">{APP_NAME}</span>
      </div>

      <div className="flex items-center gap-2 ml-2">
        <span
          className={`status-dot-online ${
            connected ? 'bg-terminal-green' : 'bg-terminal-amber'
          }`}
        />
        <span className="text-[11px] text-gray-400 uppercase tracking-wider">
          {connected ? 'sandbox connected' : 'connecting...'}
        </span>
      </div>

      <div className="ml-auto flex items-center gap-2">
        {sessionId && (
          <span className="text-[10px] px-2 py-1 rounded-md bg-white/5 border border-white/10 text-gray-400 font-mono">
            SESSION {sessionId.slice(0, 8)}
          </span>
        )}
        <div className="flex items-center gap-1.5 px-2 py-1 rounded-md bg-white/5 border border-white/10">
          <span className="text-[10px] text-gray-400">CPU</span>
          <CpuStat />
        </div>
      </div>
    </motion.div>
  );
}

function CpuStat() {
  const metrics = useSystemStore((s) => s.metrics);
  const cpu = metrics?.cpu ?? 0;
  return (
    <span className="text-[11px] font-mono text-virgo-200">
      {cpu.toFixed(0)}%
    </span>
  );
}
