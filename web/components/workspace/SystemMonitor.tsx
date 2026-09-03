'use client';

import { useEffect, useState } from 'react';
import * as Icons from 'lucide-react';
import { useSystemStore } from '@/stores/system';

type Metric = {
  label: string;
  value: number;
  unit: string;
  color: string;
};

import { Cpu, MemoryStick, HardDrive, Activity, Server, Clock } from 'lucide-react';

export function SystemMonitor() {
  const metrics = useSystemStore((s) => s.metrics);

  const stats: Metric[] = [
    { label: 'CPU', value: metrics?.cpu ?? 0, unit: '%', color: '#00d4ff' },
    {
      label: 'Memory',
      value: metrics ? ((metrics.memUsed / metrics.memTotal) * 100) : 0,
      unit: '%',
      color: '#b967ff',
    },
    {
      label: 'Disk',
      value: metrics ? ((metrics.diskUsed / metrics.diskTotal) * 100) : 0,
      unit: '%',
      color: '#00ff9c',
    },
  ];

  return (
    <div className="h-full flex flex-col bg-void-950/50 p-4 space-y-4 overflow-y-auto">
      <div className="flex items-center gap-2">
        <Activity size={14} className="text-virgo-400" />
        <span className="text-[10px] uppercase tracking-widest text-gray-500">System Monitor</span>
      </div>

      <div className="flex items-center gap-4 p-3 rounded-xl bg-white/5 border border-white/10">
        <Server size={28} className="text-virgo-400" />
        <div>
          <div className="text-sm font-semibold text-gray-200">{metrics?.hostname ?? 'virgoyt-cloud'}</div>
          <div className="text-[10px] text-gray-500">{metrics?.os ?? 'Linux (Ubuntu 22.04 LTS)'} · {metrics?.kernel ?? '6.x'}</div>
        </div>
        <div className="ml-auto flex items-center gap-1.5">
          <span className="h-2 w-2 rounded-full bg-terminal-green animate-pulse" />
          <span className="text-[10px] text-terminal-green">LIVE</span>
        </div>
      </div>

      <div className="text-[11px] text-gray-500 flex items-center gap-1.5">
        <Clock size={12} />
        Uptime: {metrics ? formatUptime(metrics.uptime) : '0h 0m'}
      </div>

      <div className="grid grid-cols-3 gap-2">
        {stats.map((s) => (
          <div key={s.label} className="p-2.5 rounded-xl bg-white/5 border border-white/10 text-center">
            <div className="text-[10px] uppercase tracking-wider text-gray-500 mb-1">{s.label}</div>
            <div className="text-xl font-bold" style={{ color: s.color }}>
              {s.value.toFixed(1)}
              <span className="text-[10px] font-normal text-gray-500 ml-0.5">{s.unit}</span>
            </div>
            <div className="mt-2 h-1 rounded-full bg-white/10 overflow-hidden">
              <div
                className="h-full rounded-full transition-all duration-500"
                style={{ width: `${Math.min(100, s.value)}%`, background: s.color }}
              />
            </div>
          </div>
        ))}
      </div>

      <div className="p-3 rounded-xl bg-white/5 border border-white/10">
        <div className="text-[10px] uppercase tracking-wider text-gray-500 mb-2">Network</div>
        <div className="grid grid-cols-2 gap-2 text-xs">
          <div className="flex items-center gap-1.5">
            <Icons.ArrowDown className="text-terminal-green" size={12} />
            <span className="text-gray-300">{formatBytes(metrics?.network.down ?? 0)}/s</span>
          </div>
          <div className="flex items-center gap-1.5">
            <Icons.ArrowUp className="text-terminal-cyan" size={12} />
            <span className="text-gray-300">{formatBytes(metrics?.network.up ?? 0)}/s</span>
          </div>
        </div>
      </div>

      <div className="p-3 rounded-xl bg-white/5 border border-white/10">
        <div className="text-[10px] uppercase tracking-wider text-gray-500 mb-2">Active Processes</div>
        <div className="space-y-1">
          {(metrics?.processes?.slice(0, 8) ?? demoProcesses()).map((p) => (
            <div key={p.pid} className="flex items-center gap-2 text-xs">
              <span className="text-gray-500 w-10">{p.pid}</span>
              <span className="text-gray-300 flex-1 truncate">{p.name}</span>
              <span className="text-gray-500">{p.cpu.toFixed(1)}%</span>
              <span className="text-gray-500">{p.mem.toFixed(1)} MB</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function formatUptime(seconds: number): string {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  return `${h}h ${m}m`;
}

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

function demoProcesses() {
  return [
    { pid: 1, name: 'systemd', cpu: 0.2, mem: 120 },
    { pid: 42, name: 'node', cpu: 12.4, mem: 185 },
    { pid: 87, name: 'python3', cpu: 3.1, mem: 94 },
    { pid: 101, name: 'nginx', cpu: 0.8, mem: 45 },
    { pid: 133, name: 'code-server', cpu: 8.2, mem: 210 },
    { pid: 189, name: 'bash', cpu: 0.3, mem: 12 },
    { pid: 233, name: 'localhost:3000', cpu: 1.5, mem: 67 },
    { pid: 291, name: 'virgoyt-agent', cpu: 21.0, mem: 340 },
  ];
}
