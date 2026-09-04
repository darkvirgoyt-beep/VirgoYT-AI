'use client';

import { useState, useRef, useEffect } from 'react';
import { Rocket, Loader2, Terminal, Globe, Download, FilePlus2, Check, X, Mic, ShieldAlert, Square } from 'lucide-react';
import { useAgentStore } from '@/stores/agent';
import { useSystemStore } from '@/stores/system';

const previewBase = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

export function AgentPanel() {
  const { events, running, pendingConfirm, init, run, confirm, setPreview, previewUrl, clear } = useAgentStore();
  const sessionId = useSystemStore((s) => s.sessionId);
  const [goal, setGoal] = useState('');
  const [showPreview, setShowPreview] = useState(false);
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (sessionId) init(sessionId);
  }, [sessionId, init]);

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' });
  }, [events]);

  const onRun = () => {
    if (!goal.trim() || !sessionId || running) return;
    run(goal, sessionId);
    setGoal('');
  };

  const iconFor = (e: { type: string }) => {
    switch (e.type) {
      case 'command': case 'tool-start': return <Terminal size={13} className="text-cyan-400" />;
      case 'browse': case 'site-preview': return <Globe size={13} className="text-emerald-400" />;
      case 'download': return <Download size={13} className="text-amber-400" />;
      case 'file-write': return <FilePlus2 size={13} className="text-violet-400" />;
      default: return <Mic size={13} className="text-pink-400" />;
    }
  };

  return (
    <div className="flex h-full flex-col bg-[#0a0d14]/80 text-sm">
      {/* Feed */}
      <div ref={scrollRef} className="flex-1 space-y-1.5 overflow-y-auto p-3 font-mono text-[11px] leading-relaxed">
        {events.length === 0 && !running && (
          <div className="mt-6 text-center text-[11px] text-white/30">
            Ask Virgo to browse, code, run terminal commands, or download files.
            <br />Watching everything live.
          </div>
        )}
        {events.map((e, i) => {
          if (e.type === 'plan') {
            return (
              <div key={i} className="rounded-md border border-white/5 bg-white/[0.03] p-2">
                <div className="mb-1 text-[10px] uppercase tracking-wider text-white/40">Plan</div>
                <ol className="list-decimal pl-4 text-white/70">
                  {e.steps.map((s, j) => (
                    <li key={j}>{s}</li>
                  ))}
                </ol>
              </div>
            );
          }
          if (e.type === 'command') {
            return (
              <div key={i} className="rounded border border-cyan-500/20 bg-cyan-500/5 px-2 py-1.5">
                <div className="flex items-center gap-1.5 text-cyan-300">
                  <Terminal size={12} /> {e.command}
                </div>
                {e.output && <div className="mt-1 whitespace-pre-wrap text-white/50">{truncate(e.output, 300)}</div>}
              </div>
            );
          }
          if (e.type === 'browse' || e.type === 'site-preview') {
            return (
              <button
                key={i}
                onClick={() => {
                  setPreview(e.url);
                  setShowPreview(true);
                }}
                className="flex w-full items-center gap-1.5 rounded border border-emerald-500/20 bg-emerald-500/5 px-2 py-1.5 text-left text-emerald-300 hover:bg-emerald-500/10"
              >
                <Globe size={12} /> {e.url}
                <span className="ml-auto text-[9px] text-white/30">view</span>
              </button>
            );
          }
          if (e.type === 'download') {
            return (
              <div key={i} className="flex items-center gap-1.5 rounded border border-amber-500/20 bg-amber-500/5 px-2 py-1 text-amber-300">
                <Download size={12} /> {e.path} <span className="ml-auto text-[9px] text-white/30">{fmt(e.size)}</span>
              </div>
            );
          }
          if (e.type === 'file-write') {
            return (
              <div key={i} className="flex items-center gap-1.5 rounded border border-violet-500/20 bg-violet-500/5 px-2 py-1 text-violet-300">
                <FilePlus2 size={12} /> {e.path}
              </div>
            );
          }
          if (e.type === 'thought') {
            return (
              <div key={i} className="flex items-start gap-1.5 text-white/60">
                <span className="mt-0.5 text-pink-400">✦</span> {e.text}
              </div>
            );
          }
          if (e.type === 'tool-start') {
            return (
              <div key={i} className="flex items-center gap-1.5 text-white/40">
                {iconFor(e)} running <span className="text-white/70">{e.tool}</span>
              </div>
            );
          }
          if (e.type === 'result') {
            return <div key={i} className="rounded border border-emerald-500/30 bg-emerald-500/10 px-2 py-1.5 text-emerald-200">✓ {e.summary}</div>;
          }
          if (e.type === 'error') {
            return <div key={i} className="rounded border border-red-500/30 bg-red-500/10 px-2 py-1.5 text-red-300">✕ {e.message}</div>;
          }
          return null;
        })}
        {running && (
          <div className="flex items-center gap-2 py-1 text-white/40">
            <Loader2 size={13} className="animate-spin" /> thinking…
          </div>
        )}
      </div>

      {/* Preview */}
      {showPreview && previewUrl && (
        <div className="border-t border-white/5 p-2" style={{ height: '38%' }}>
          <div className="mb-1 flex items-center justify-between">
            <span className="truncate text-[10px] text-emerald-300">{previewUrl}</span>
            <div className="flex items-center gap-1">
              <button onClick={() => setPreview(null)} className="rounded px-1.5 py-0.5 text-[10px] text-white/40 hover:bg-white/5">new</button>
              <button onClick={() => setShowPreview(false)} className="rounded px-1.5 py-0.5 text-[10px] text-white/40 hover:bg-white/5">✕</button>
            </div>
          </div>
          <iframe
            src={`${previewBase}/api/browser/preview?url=${encodeURIComponent(previewUrl)}`}
            className="h-[calc(100%-24px)] w-full rounded border border-white/10 bg-white"
            sandbox="allow-scripts allow-same-origin allow-forms"
            title="browser preview"
          />
        </div>
      )}

      {/* Confirmation */}
      {pendingConfirm && (
        <div className="border-t border-amber-500/30 bg-amber-500/10 p-2">
          <div className="flex items-center gap-1.5 text-amber-300">
            <ShieldAlert size={12} /> Confirm action?
          </div>
          <div className="mt-1 rounded bg-black/40 p-1.5 font-mono text-[11px] break-words text-white/80">{pendingConfirm.action}</div>
          <div className="mt-1 flex gap-2">
            <button
              onClick={() => confirm(true)}
              className="flex flex-1 items-center justify-center gap-1 rounded bg-emerald-600/80 py-1 text-[11px] text-white hover:bg-emerald-600"
            >
              <Check size={12} /> Allow
            </button>
            <button
              onClick={() => confirm(false)}
              className="flex flex-1 items-center justify-center gap-1 rounded bg-red-600/80 py-1 text-[11px] text-white hover:bg-red-600"
            >
              <X size={12} /> Deny
            </button>
          </div>
        </div>
      )}

      {/* Input */}
      <div className="border-t border-white/5 p-2">
        <div className="flex items-end gap-1.5 rounded-lg border border-white/10 bg-black/40 px-2 py-1.5 focus-within:border-cyan-500/40">
          <textarea
            value={goal}
            onChange={(e) => setGoal(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                onRun();
              }
            }}
            placeholder={`Tell Virgo what to do… e.g. "open github.com/login in the browser", "download latest linux .apk", "run npm start"`}
            rows={2}
            className="max-h-20 w-full resize-none bg-transparent text-white placeholder:text-white/25 focus:outline-none"
          />
          {running ? (
            <button onClick={clear} className="rounded bg-white/10 p-1.5 text-white/70 hover:bg-white/20">
              <Square size={14} />
            </button>
          ) : (
            <button onClick={onRun} disabled={!goal.trim() || !sessionId} className="rounded bg-cyan-600 p-1.5 text-white hover:bg-cyan-500 disabled:opacity-30">
              <Rocket size={14} />
            </button>
          )}
        </div>
        <div className="mt-1 text-[9px] text-white/25">Virgo watches your GitHub login, browser, terminal, and downloads — and asks before anything risky.</div>
      </div>
    </div>
  );
}

function truncate(s: string, n: number) {
  return s.length > n ? s.slice(0, n) + '…' : s;
}
function fmt(bytes: number) {
  if (!bytes) return '';
  if (bytes > 1e9) return (bytes / 1e9).toFixed(1) + ' GB';
  if (bytes > 1e6) return (bytes / 1e6).toFixed(1) + ' MB';
  if (bytes > 1e3) return (bytes / 1e3).toFixed(1) + ' KB';
  return bytes + ' B';
}