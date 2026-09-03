'use client';

import { useState, useCallback } from 'react';
import * as Icons from 'lucide-react';
import { ArrowLeft, ArrowRight, RotateCw, Globe, Lock, MonitorSmartphone } from 'lucide-react';

type ViewportMode = 'desktop' | 'tablet' | 'mobile';

const VIEWPORTS: Record<ViewportMode, { w: number; h: number }> = {
  desktop: { w: 1200, h: 800 },
  tablet: { w: 768, h: 1024 },
  mobile: { w: 390, h: 844 },
};

export function BrowserSandbox() {
  const [url, setUrl] = useState('https://');
  const [mode, setMode] = useState<ViewportMode>('desktop');
  const [history, setHistory] = useState<string[]>([]);
  const [historyIndex, setHistoryIndex] = useState(-1);
  const [address, setAddress] = useState('https://example.com');
  const [loading, setLoading] = useState(false);

  const navigate = useCallback(
    (target: string) => {
      const clean = target.startsWith('http') ? target : `https://${target}`;
      setLoading(true);
      setAddress(clean);
      setHistory((h) => [...h.slice(0, historyIndex + 1), clean]);
      setHistoryIndex((i) => i + 1);
      setTimeout(() => setLoading(false), 1200);
    },
    [historyIndex]
  );

  const goBack = () => {
    if (historyIndex > 0) {
      const prev = history[historyIndex - 1];
      setHistoryIndex(historyIndex - 1);
      setAddress(prev);
    }
  };

  const goForward = () => {
    if (historyIndex < history.length - 1) {
      const next = history[historyIndex + 1];
      setHistoryIndex(historyIndex + 1);
      setAddress(next);
    }
  };

  const reload = () => {
    navigate(address);
  };

  const viewport = VIEWPORTS[mode];

  return (
    <div className="h-full flex flex-col bg-void-950/50">
      <div className="flex items-center gap-1.5 px-2 py-1.5 border-b border-white/5">
        <button onClick={goBack} disabled={historyIndex <= 0} className="p-1 rounded hover:bg-white/10 disabled:opacity-30">
          <ArrowLeft size={14} />
        </button>
        <button onClick={goForward} disabled={historyIndex >= history.length - 1} className="p-1 rounded hover:bg-white/10 disabled:opacity-30">
          <ArrowRight size={14} />
        </button>
        <button onClick={reload} className="p-1 rounded hover:bg-white/10">
          <RotateCw size={13} className={loading ? 'animate-spin' : ''} />
        </button>
        <div className="flex-1 flex items-center gap-1.5 mx-1 px-2 py-1 rounded-md bg-white/5 border border-white/10 text-xs text-gray-400">
          <Lock size={11} className="text-terminal-green" />
          <span className="truncate">{address}</span>
        </div>
        <select
          value={mode}
          onChange={(e) => setMode(e.target.value as ViewportMode)}
          className="bg-white/5 border border-white/10 rounded px-1 py-0.5 text-[10px] text-gray-400"
        >
          <option value="desktop">Desktop</option>
          <option value="tablet">Tablet</option>
          <option value="mobile">Mobile</option>
        </select>
      </div>

      <div className="flex-1 flex items-center justify-center bg-[radial-gradient(circle_at_center,rgba(51,117,255,0.05),transparent_70%)] overflow-hidden p-3">
        <div
          className="bg-white rounded-lg shadow-2xl overflow-hidden transition-all duration-500"
          style={{ width: viewport.w * 0.55, height: viewport.h * 0.55 }}
        >
          <div className="flex items-center gap-1 bg-gray-100 px-2.5 py-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-red-400" />
            <span className="w-2.5 h-2.5 rounded-full bg-yellow-400" />
            <span className="w-2.5 h-2.5 rounded-full bg-green-400" />
            <div className="flex-1 ml-2 flex items-center gap-1 bg-white rounded px-1.5 py-0.5 text-[10px] text-gray-500">
              <Globe size={10} className="text-gray-400" />
              <span className="truncate">{address}</span>
            </div>
          </div>
          <div className="bg-[#f8fafc] flex items-center justify-center relative" style={{ height: 'calc(100% - 28px)' }}>
            {loading ? (
              <div className="flex flex-col items-center gap-2 text-gray-400">
                <div className="w-8 h-8 border-2 border-virgo-400 border-t-transparent rounded-full animate-spin" />
                <span className="text-[11px]">Connecting to sandbox...</span>
              </div>
            ) : (
              <div className="text-center text-gray-400 p-6">
                <MonitorSmartphone size={40} className="mx-auto mb-3 text-virgo-300" />
                <div className="text-sm font-medium text-gray-600">{address}</div>
                <div className="text-[11px] mt-1">Cloud browser — connect backend to view live apps</div>
                <div className="mt-4 flex justify-center gap-2">
                  <input
                    value={url}
                    onChange={(e) => setUrl(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && navigate(url)}
                    placeholder="Enter a URL to browse"
                    className="text-[12px] px-3 py-1.5 rounded-md border border-gray-300 focus:outline-none focus:border-virgo-400 w-64"
                  />
                  <button
                    onClick={() => navigate(url)}
                    className="text-[12px] px-3 py-1.5 rounded-md bg-virgo-600 text-white hover:bg-virgo-500"
                  >
                    Browse
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
