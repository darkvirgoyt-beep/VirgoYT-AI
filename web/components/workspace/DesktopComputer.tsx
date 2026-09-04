'use client';

import { useMemo, useState } from 'react';
import { Monitor, ExternalLink, Maximize2, RefreshCw, ShieldCheck } from 'lucide-react';

const desktopUrl = process.env.NEXT_PUBLIC_DESKTOP_URL?.trim() ?? '';

export function DesktopComputer() {
  const [reloadKey, setReloadKey] = useState(0);
  const [fullscreen, setFullscreen] = useState(false);
  const title = useMemo(() => (desktopUrl ? 'VirgoYT Computer — live desktop' : 'VirgoYT Computer — waiting for host'), []);

  return (
    <div className={`h-full flex flex-col bg-void-950/80 ${fullscreen ? 'fixed inset-0 z-[20000]' : ''}`}>
      <div className="flex items-center gap-2 px-3 py-2 border-b border-white/10 bg-void-900/90">
        <Monitor size={14} className="text-terminal-cyan" />
        <span className="text-xs text-gray-200 truncate">{title}</span>
        <span className="ml-auto flex items-center gap-1 text-[10px] text-terminal-green">
          <ShieldCheck size={12} /> secured session
        </span>
        {desktopUrl && (
          <>
            <button
              onClick={() => setReloadKey((key) => key + 1)}
              className="p-1 rounded hover:bg-white/10 text-gray-400 hover:text-white"
              title="Reload desktop"
            >
              <RefreshCw size={13} />
            </button>
            <button
              onClick={() => setFullscreen((value) => !value)}
              className="p-1 rounded hover:bg-white/10 text-gray-400 hover:text-white"
              title="Toggle fullscreen"
            >
              <Maximize2 size={13} />
            </button>
            <a
              href={desktopUrl}
              target="_blank"
              rel="noreferrer"
              className="p-1 rounded hover:bg-white/10 text-gray-400 hover:text-white"
              title="Open desktop in a new tab"
            >
              <ExternalLink size={13} />
            </a>
          </>
        )}
      </div>

      <div className="flex-1 min-h-0 bg-black">
        {desktopUrl ? (
          <iframe
            key={reloadKey}
            title="VirgoYT Computer desktop"
            src={desktopUrl}
            className="h-full w-full border-0"
            allow="clipboard-read; clipboard-write; fullscreen"
          />
        ) : (
          <div className="h-full flex items-center justify-center p-8 text-center bg-[radial-gradient(circle_at_center,rgba(0,212,255,0.08),transparent_60%)]">
            <div className="max-w-md">
              <Monitor size={56} className="mx-auto mb-5 text-terminal-cyan/80" />
              <h3 className="text-lg font-semibold text-white">VirgoYT Computer is ready for its host</h3>
              <p className="mt-3 text-sm leading-relaxed text-gray-400">
                Deploy the isolated desktop service on Railway, then set <code className="text-terminal-cyan">NEXT_PUBLIC_DESKTOP_URL</code> to its private HTTPS URL.
              </p>
              <p className="mt-4 text-xs text-gray-600">The desktop is intentionally disabled until a protected host is configured.</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
