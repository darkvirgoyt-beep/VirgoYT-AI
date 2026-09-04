'use client';

import { useState, useRef, useEffect } from 'react';
import { Rocket, Loader2, Terminal, Globe, Download, FilePlus2, Check, X, Mic, ShieldAlert, Square, Plug, Package, Server, Users, Factory, Sparkles, Cpu } from 'lucide-react';
import { useAgentStore } from '@/stores/agent';
import { useSystemStore } from '@/stores/system';
import type { ScanReport, ConnectorState, ExportTarget } from '@/stores/agent';

const previewBase = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

type Caps = { mcp: { servers: string[]; tools: { server: string; tool: string }[] }; plugins: { id: string; name: string; tools: string[] }[] };
type Mode = 'agent' | 'team' | 'factory' | 'devtools';

export function AgentPanel() {
  const { events, wfEvents, fxEvents, roster, running, pendingConfirm, init, run, confirm, setPreview, previewUrl, clear, loadRoster, runWorkforce, runFactory, runScan, scan, exportProject, connectors, loadConnectors, labs, loadLabs } = useAgentStore();
  const sessionId = useSystemStore((s) => s.sessionId);
  const [goal, setGoal] = useState('');
  const [mode, setMode] = useState<Mode>('agent');
  const [showPreview, setShowPreview] = useState(false);
  const [caps, setCaps] = useState<Caps | null>(null);
  const [scanning, setScanning] = useState(false);
  const [exporting, setExporting] = useState<string | null>(null);
  const [exportMsg, setExportMsg] = useState<string | null>(null);
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (sessionId) init(sessionId);
    loadRoster();
    loadConnectors();
    loadLabs();
  }, [sessionId, init, loadRoster, loadConnectors, loadLabs]);

  useEffect(() => {
    fetch(`${previewBase}/api/agent/capabilities`)
      .then((r) => r.json().catch(() => null))
      .then((d) => d && setCaps(d))
      .catch(() => {});
  }, []);

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' });
  }, [events, wfEvents, fxEvents]);

  const onRun = () => {
    if (!goal.trim() || !sessionId || running) return;
    if (mode === 'team') runWorkforce(goal, sessionId);
    else if (mode === 'factory') runFactory(goal, sessionId);
    else run(goal, sessionId);
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
      {/* Mode tabs */}
      <div className="flex items-center gap-1 border-b border-white/5 px-2 py-1">
        {([
          ['agent', 'Agent', Rocket],
          ['team', 'Workforce', Users],
          ['factory', 'Factory', Factory],
          ['devtools', 'Build & Deploy', Cpu],
        ] as [Mode, string, any][]).map(([m, label, Icon]) => (
          <button
            key={m}
            onClick={() => setMode(m)}
            className={`flex flex-1 items-center justify-center gap-1 rounded-md py-1 text-[10px] ${
              mode === m ? 'bg-cyan-500/20 text-cyan-300' : 'text-white/40 hover:bg-white/5'
            }`}
          >
            <Icon size={11} /> {label}
          </button>
        ))}
      </div>
      {/* Feed */}
      <div ref={scrollRef} className="flex-1 space-y-1.5 overflow-y-auto p-3 font-mono text-[11px] leading-relaxed">
        {mode === 'agent' && events.length === 0 && !running && (
          <div className="mt-6 text-center text-[11px] text-white/30">
            Ask Virgo to browse, code, run terminal commands, or download files.
            <br />Watching everything live.
          </div>
        )}
        {mode === 'team' && wfEvents.length === 0 && (
          <div className="mt-6 text-center text-[11px] text-white/30">
            A supervisor orchestrates {roster.length} specialist agents to research, design, build and ship.
            <br />Type a goal and press the rocket — or pick one below.
          </div>
        )}
        {mode === 'factory' && fxEvents.length === 0 && (
          <div className="mt-6 text-center text-[11px] text-white/30">
            Describe a product — "build me a todo web app" — and the factory will
            <br />plan → scaffold → code → install → document → ship it to your workspace.
          </div>
        )}
        {mode === 'team' && roster.length > 0 && wfEvents.length === 0 && (
          <div className="flex flex-wrap gap-1 py-1">
            {roster.map((a) => (
              <span key={a.id} className="rounded bg-white/5 px-1.5 py-0.5 text-[9px] text-white/60">
                {a.emoji} {a.name}
              </span>
            ))}
          </div>
        )}
        {mode === 'team' &&
          wfEvents.map((e, i) => {
            if (e.type === 'wf-agent-start') {
              const a = roster.find((r) => r.id === e.agent);
              return (
                <div key={i} className="flex items-center gap-1.5 text-white/50">
                  <Loader2 size={11} className="animate-spin text-cyan-400" /> {a?.emoji ?? ''} <b className="text-cyan-300">{a?.name ?? e.agent}</b> working…
                  <span className="ml-auto text-[9px] text-white/25">▶</span>
                </div>
              );
            }
            if (e.type === 'wf-agent-done') {
              const a = roster.find((r) => r.id === e.agent);
              return (
                <div key={i} className="rounded border border-emerald-500/20 bg-emerald-500/5 px-2 py-1">
                  <div className="flex items-center gap-1.5 text-emerald-300">
                    <Check size={12} /> {a?.emoji ?? ''} {a?.name ?? e.agent} finished
                  </div>
                  <div className="mt-1 whitespace-pre-wrap text-white/60">{truncate(e.output, 500)}</div>
                </div>
              );
            }
            if (e.type === 'wf-result') {
              return <div key={i} className="rounded border border-cyan-500/30 bg-cyan-500/10 px-2 py-1.5 text-cyan-200">✓ {e.summary}</div>;
            }
            if (e.type === 'wf-error') {
              return <div key={i} className="rounded border border-red-500/30 bg-red-500/10 px-2 py-1.5 text-red-300">✕ {e.message}</div>;
            }
            return null;
          })}
        {mode === 'factory' &&
          fxEvents.map((e, i) => {
            if (e.type === 'factory-stage') {
              return (
                <div key={i} className="flex items-center gap-1.5 text-white/60">
                  <Sparkles size={11} className="text-pink-400" /> <b className="uppercase text-pink-300">{e.stage}</b> — {e.message}
                </div>
              );
            }
            if (e.type === 'factory-file') {
              return (
                <div key={i} className="flex items-center gap-1.5 rounded border border-violet-500/20 bg-violet-500/5 px-2 py-1 text-violet-300">
                  <FilePlus2 size={12} /> {e.path}
                </div>
              );
            }
            if (e.type === 'factory-command') {
              return (
                <div key={i} className="rounded border border-cyan-500/20 bg-cyan-500/5 px-2 py-1 text-cyan-300">
                  <Terminal size={11} /> <span className="text-cyan-200">{e.command}</span>
                  <div className="mt-0.5 whitespace-pre-wrap text-white/40">{truncate(e.output, 200)}</div>
                </div>
              );
            }
            if (e.type === 'factory-result') {
              return <div key={i} className="rounded border border-emerald-500/30 bg-emerald-500/10 px-2 py-1.5 text-emerald-200">✓ {e.summary}</div>;
            }
            if (e.type === 'factory-error') {
              return <div key={i} className="rounded border border-red-500/30 bg-red-500/10 px-2 py-1.5 text-red-300">✕ {e.message}</div>;
            }
            return null;
          })}
        {mode === 'agent' && events.map((e, i) => {
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
        {mode === 'devtools' && <DevToolsDoc
          sessionId={sessionId}
          scanning={scanning}
          setScanning={setScanning}
          scan={scan}
          runScan={runScan}
          exporting={exporting}
          setExporting={setExporting}
          exportProject={exportProject}
          setExportMsg={setExportMsg}
          exportMsg={exportMsg}
          connectors={connectors}
          labs={labs}
        />}
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

      {/* Capabilities */}
      {caps && (
        <div className="border-t border-white/5 px-2 py-1 text-[9px] text-white/35">
          <Plug size={10} className="mr-0.5" />
          MCP {caps.mcp.servers.length ? caps.mcp.servers.join(', ') : 'off'}
          <span className="ml-1.5 text-white/20">•</span>
          <Package size={10} className="mr-0.5" />
          Plugins {caps.plugins.length ? caps.plugins.map((p) => p.name).join(', ') : 'none'}
          <span className="ml-1.5 text-white/20">•</span>
          <Server size={10} className="mr-0.5" />
          API {caps.mcp.tools.length + caps.plugins.reduce((n, p) => n + p.tools.length, 0)} tools
        </div>
      )}

      {/* Input */}
      {mode !== 'devtools' && <div className="border-t border-white/5 p-2">
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
      </div>}
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

const EXPORT_TARGETS: { id: ExportTarget; label: string; icon: any }[] = [
  { id: 'web', label: 'Live Web', icon: Globe },
  { id: 'exe', label: '.exe (Win)', icon: Square },
  { id: 'apk', label: '.apk (Android)', icon: Package },
  { id: 'mac', label: '.app (macOS)', icon: Server },
  { id: 'terminal', label: 'Terminal / source', icon: Terminal },
];

type DevToolsDocProps = {
  sessionId: string | null;
  scanning: boolean;
  setScanning: (b: boolean) => void;
  scan: ScanReport | null;
  runScan: (sessionId: string) => Promise<void>;
  exporting: string | null;
  setExporting: (t: string | null) => void;
  exportProject: (s: string, t: ExportTarget, app?: string) => Promise<string | null>;
  setExportMsg: (m: string | null) => void;
  exportMsg: string | null;
  connectors: ConnectorState[];
  labs: { id: string; title: string }[];
};

function DevToolsDoc(p: DevToolsDocProps) {
  const sevColor = { critical: 'text-red-400', high: 'text-orange-400', medium: 'text-amber-300', low: 'text-white/60', info: 'text-white/40' } as Record<string, string>;
  const doScan = () => { const sid = p.sessionId; if (!sid || p.scanning) return; p.setScanning(true); Promise.resolve().then(() => p.runScan(sid)).finally(() => p.setScanning(false)); };
  const doExport = async (t: ExportTarget) => { const sid = p.sessionId; if (!sid || p.exporting) return; p.setExporting(t); p.setExportMsg(null); const out = await p.exportProject(sid, t); p.setExporting(null); p.setExportMsg(out ? `Exported ${t} bundle → ${out}` : `Export ${t} failed (no workspace)`); };

  return (
    <div className="space-y-2">
      {/* Security scan */}
      <div className="rounded border border-white/10 bg-white/[0.03] p-2">
        <div className="flex items-center gap-1.5 text-white/70"><ShieldAlert size={12} className="text-red-400" /> <b>Cyber Defense — audit your workspace</b></div>
        <p className="mt-0.5 text-[10px] text-white/40">Scans YOUR code for exposed secrets & known-vulnerable deps. Defensive only.</p>
        <button onClick={doScan} disabled={!p.sessionId || p.scanning} className="mt-1 rounded border border-red-500/40 bg-red-500/10 px-2 py-1 text-[10px] text-red-300 hover:bg-red-500/20">
          {p.scanning ? 'Scanning…' : '🔍 Run security scan'}
        </button>
        {p.scan && (
          <div className="mt-1.5">
            <div className="text-[10px] text-white/50">{p.scan.scanned} files scanned — {p.scan.summary}</div>
            <div className="max-h-40 overflow-y-auto">
              {p.scan.findings.map((f, i) => (
                <div key={i} className={`flex gap-1.5 text-[10px] ${sevColor[f.severity]}`}>
                  <span>{f.file ? `${f.file}:${f.line ?? ''}` : f.category}</span>
                  <span className="flex-1">{f.title}</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Build & Deploy */}
      <div className="rounded border border-white/10 bg-white/[0.03] p-2">
        <div className="flex items-center gap-1.5 text-white/70"><Cpu size={12} className="text-cyan-400" /> <b>Build & Deploy — ship your workspace</b></div>
        <div className="mt-1 flex flex-wrap gap-1.5">
          {EXPORT_TARGETS.map(({ id, label, icon: Icon }) => (
            <button key={id} onClick={() => doExport(id)} disabled={!p.sessionId || p.exporting !== null} className="rounded border border-cyan-500/30 bg-cyan-500/10 px-2 py-1 text-[10px] text-cyan-300 hover:bg-cyan-500/20">
              <Icon size={11} className="mr-1" />{p.exporting === id ? '…' : label}
            </button>
          ))}
        </div>
        {p.exportMsg && <div className="mt-1 truncate text-[9px] text-white/40">{p.exportMsg}</div>}
      </div>

      {/* Connectors */}
      <div className="rounded border border-white/10 bg-white/[0.03] p-2">
        <div className="flex items-center gap-1.5 text-white/70"><Plug size={12} className="text-emerald-400" /> <b>Deploy connectors</b></div>
        {p.connectors.length === 0 && <div className="text-[10px] text-white/30">No OAuth clients configured.</div>}
        <div className="mt-1 grid grid-cols-2 gap-1.5">
          {p.connectors.map((c) => (
            <div key={c.id} className="rounded bg-white/5 px-2 py-1 text-[10px]">
              <span className={c.configured ? 'text-emerald-400' : 'text-white/35'}>●</span> {c.name}
              <span className="ml-1 text-white/30">{c.configured ? (c.authUrl ? 'ready' : 'auth') : 'unset'}</span>
              {c.configured && c.authUrl && <a href={c.authUrl} className="ml-1 text-[9px] underline text-cyan-400">connect</a>}
            </div>
          ))}
        </div>
      </div>

      {/* Ethical labs */}
      <div className="rounded border border-white/10 bg-white/[0.03] p-2">
        <div className="flex items-center gap-1.5 text-white/70"><Check size={12} className="text-violet-400" /> <b>Ethical hacking labs (defensive)</b></div>
        {p.labs.length === 0 && <div className="text-[10px] text-white/30">No labs loaded.</div>}
        <div className="mt-1 space-y-1">
          {p.labs.map((l) => (
            <div key={l.id} className="flex gap-1.5 text-[10px] text-white/60">
              <span className="text-violet-400">›</span> {l.title}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}