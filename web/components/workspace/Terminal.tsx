'use client';

import { useEffect, useRef, useState } from 'react';
import type { Terminal as XTerm } from '@xterm/xterm';
import type { FitAddon } from '@xterm/addon-fit';
import '@xterm/xterm/css/xterm.css';
import { useTerminalStore } from '@/stores/terminal';
import { api } from '@/lib/api';

type TerminalViewProps = {
  tabId: string;
};

export function TerminalView({ tabId }: TerminalViewProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const termRef = useRef<XTerm | null>(null);
  const fitRef = useRef<FitAddon | null>(null);
  const [status, setStatus] = useState<'connecting' | 'connected' | 'offline'>('connecting');
  const writeLine = useTerminalStore((s) => s.writeLine);
  const setConnected = useTerminalStore((s) => s.setConnected);

  useEffect(() => {
    let disposed = false;
    let term: XTerm | null = null;
    let fit: FitAddon | null = null;
    let resizeObserver: ResizeObserver | null = null;
    let removeResizeListener: (() => void) | null = null;
    let disposeDataListener: (() => void) | null = null;

    const initializeTerminal = async () => {
      if (!containerRef.current) return;

      // xterm and addon-fit access browser globals during module evaluation.
      // Import them after mount so Next.js never evaluates them during SSR.
      const [{ Terminal }, { FitAddon }] = await Promise.all([
        import('@xterm/xterm'),
        import('@xterm/addon-fit'),
      ]);
      if (disposed || !containerRef.current) return;

      term = new Terminal({
        cursorBlink: true,
        fontSize: 13,
        fontFamily: '"JetBrains Mono", ui-monospace, monospace',
        theme: {
          background: '#0a0c1a',
          foreground: '#e6edf3',
          cursor: '#00ff9c',
          cursorAccent: '#0a0c1a',
          selectionBackground: 'rgba(51,117,255,0.4)',
          black: '#0a0c1a',
          red: '#ff5c7a',
          green: '#00ff9c',
          yellow: '#ffb547',
          blue: '#3375ff',
          magenta: '#b967ff',
          cyan: '#00d4ff',
          white: '#e6edf3',
        },
        scrollback: 5000,
      });

      fit = new FitAddon();
      term.loadAddon(fit);
      term.open(containerRef.current);
      fit.fit();
      termRef.current = term;
      fitRef.current = fit;

      term.writeln('\x1b[38;5;39mVIRGOYT CLOUD AI TERMINAL\x1b[0m');
      term.writeln('\x1b[90mConnecting to cloud sandbox...\x1b[0m');
      term.write('\x1b[38;5;46mvirgoyt@cloud\x1b[0m:\x1b[38;5;39m~\x1b[0m$ ');

      setConnected(true);
      setStatus('connected');

      const handleResize = () => fit?.fit();
      window.addEventListener('resize', handleResize);
      removeResizeListener = () => window.removeEventListener('resize', handleResize);

      resizeObserver = new ResizeObserver(() => fit?.fit());
      resizeObserver.observe(containerRef.current);

      const dataDisposable = term.onData((data) => {
        handleLocalCommand(term!, data);
      });
      disposeDataListener = () => dataDisposable.dispose();
    };

    void initializeTerminal().catch(() => {
      if (!disposed) setStatus('offline');
    });

    return () => {
      disposed = true;
      removeResizeListener?.();
      resizeObserver?.disconnect();
      disposeDataListener?.();
      term?.dispose();
      termRef.current = null;
      fitRef.current = null;
      setConnected(false);
    };
  }, [tabId, setConnected]);

  const handleLocalCommand = (term: XTerm, data: string) => {
    if (data === '\r') {
      const buffer = term.buffer.active;
      const line = buffer.getLine(buffer.cursorY)?.translateToString(true) ?? '';
      const cmd = line.replace(/.*\$\s*/, '').trim();
      writeLine(tabId, 'input', cmd);
      if (cmd) {
        void executeLocal(term, cmd);
      }
      term.write('\x1b[38;5;46mvirgoyt@cloud\x1b[0m:\x1b[38;5;39m~\x1b[0m$ ');
    }
  };

  const executeLocal = async (term: XTerm, cmd: string) => {
    try {
      const res = await api.post<any>('/terminal/command', { command: cmd });
      if (res?.output) {
        term.write('\r\n' + res.output);
        writeLine(tabId, 'output', res.output.trim());
      } else {
        term.write('\r\n\x1b[90m(command executed locally, connect backend for full shell)\x1b[0m');
      }
    } catch {
      term.write('\r\n\x1b[90mLocal mode: Run `npm start` or docker to enable full shell.\x1b[0m');
    }
  };

  return (
    <div className="relative h-full w-full bg-void-950/60">
      <div className="flex items-center gap-2 px-3 py-1 text-[10px] text-gray-400 border-b border-white/5">
        <span className="flex items-center gap-1.5">
          <span
            className={`h-1.5 w-1.5 rounded-full ${
              status === 'connected' ? 'bg-terminal-green animate-pulse' : 'bg-terminal-amber animate-pulse'
            }`}
          />
          {status === 'connected' ? 'sandbox online' : status === 'offline' ? 'offline' : 'connecting...'}
        </span>
        <span className="ml-auto">bash — 80×24</span>
      </div>
      <div className="p-2 h-[calc(100%-28px)]">
        <div ref={containerRef} className="h-full w-full" />
      </div>
    </div>
  );
}
