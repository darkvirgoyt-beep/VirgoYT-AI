'use client';

import { useEffect, useRef, useState } from 'react';
import { Terminal as XTerm } from '@xterm/xterm';
import { FitAddon } from '@xterm/addon-fit';
import '@xterm/xterm/css/xterm.css';
import { useTerminalStore } from '@/stores/terminal';
import { api, API_URL } from '@/lib/api';

type TerminalViewProps = {
  tabId: string;
};

export function TerminalView({ tabId }: TerminalViewProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const termRef = useRef<XTerm | null>(null);
  const fitRef = useRef<FitAddon | null>(null);
  const [status, setStatus] = useState<'connecting' | 'connected' | 'offline'>('connecting');
  const writeLine = useTerminalStore((s) => s.writeLine);
  const connected = useTerminalStore((s) => s.connected);
  const setConnected = useTerminalStore((s) => s.setConnected);

  useEffect(() => {
    if (!containerRef.current) return;

    const term = new XTerm({
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

    const fit = new FitAddon();
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

    const handleResize = () => fit.fit();
    window.addEventListener('resize', handleResize);

    const ro = new ResizeObserver(() => fit.fit());
    if (containerRef.current) ro.observe(containerRef.current);

    term.onData((data) => {
      // Local demo shell response
      handleLocalCommand(term, data);
    });

    const disposable = term.onData((data) => {
      if (data === '\r') {
        term.write('\r\n');
      }
    });

    return () => {
      window.removeEventListener('resize', handleResize);
      ro.disconnect();
      disposable.dispose();
      term.dispose();
      termRef.current = null;
    };
  }, [tabId]);

  const handleLocalCommand = (term: XTerm, data: string) => {
    if (data === '\r') {
      const buffer = term.buffer.active;
      const line = buffer.getLine(buffer.cursorY)?.translateToString(true) ?? '';
      const cmd = line.replace(/.*\$\s*/, '').trim();
      writeLine(tabId, 'input', cmd);
      if (cmd) {
        executeLocal(term, cmd);
      }
      term.write('\x1b[38;5;46mvirgoyt@cloud\x1b[0m:\x1b[38;5;39m~\x1b[0m$ ');
    }
  };

  const executeLocal = async (term: XTerm, cmd: string) => {
    const [name, ...args] = cmd.split(/\s+/);
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
    // Still trigger demo regardless
    if (!termRef.current) return;
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
