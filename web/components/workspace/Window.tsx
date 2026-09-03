'use client';

import { useRef, useState, useCallback, ReactNode } from 'react';
import { motion } from 'framer-motion';
import * as Icons from 'lucide-react';
import { PanelId } from '@/stores/workspace';
import { useWorkspaceStore } from '@/stores/workspace';

type WindowProps = {
  panelId: PanelId;
  children: ReactNode;
  title: string;
  icon: string;
  accent?: string;
  onFocus?: () => void;
  toolbar?: ReactNode;
};

export function Window({ panelId, children, title, icon, accent = '#3375ff', onFocus, toolbar }: WindowProps) {
  const panel = useWorkspaceStore((s) => s.panels[panelId]);
  const focusPanel = useWorkspaceStore((s) => s.focusPanel);
  const movePanel = useWorkspaceStore((s) => s.movePanel);
  const resizePanel = useWorkspaceStore((s) => s.resizePanel);
  const hidePanel = useWorkspaceStore((s) => s.hidePanel);
  const minimizePanel = useWorkspaceStore((s) => s.minimizePanel);
  const maximizePanel = useWorkspaceStore((s) => s.maximizePanel);

  const [dragStart, setDragStart] = useState<{ x: number; y: number; px: number; py: number } | null>(null);
  const [resizing, setResizing] = useState<{ x: number; y: number; w: number; h: number } | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const Icon = (Icons as Record<string, any>)[icon] ?? Icons.Box;

  if (!panel) return null;

  const { x, y, w, h, z, minimized, maximized } = panel.position;

  const handlePointerDown = (e: React.PointerEvent) => {
    onFocus?.();
    setDragStart({ x: e.clientX, y: e.clientY, px: x, py: y });
    (e.target as HTMLElement).setPointerCapture(e.pointerId);
  };

  const handlePointerMove = (e: React.PointerEvent) => {
    if (dragStart) {
      const dx = e.clientX - dragStart.x;
      const dy = e.clientY - dragStart.y;
      movePanel(panelId, dragStart.px + dx, dragStart.py + dy);
    }
    if (resizing) {
      const dx = e.clientX - resizing.x;
      const dy = e.clientY - resizing.y;
      resizePanel(panelId, Math.max(320, resizing.w + dx), Math.max(240, resizing.h + dy));
    }
  };

  const handlePointerUp = () => {
    setDragStart(null);
    setResizing(null);
  };

  const handleResizeStart = (e: React.PointerEvent) => {
    onFocus?.();
    setResizing({ x: e.clientX, y: e.clientY, w, h });
    (e.target as HTMLElement).setPointerCapture(e.pointerId);
  };

  const startResizing = useCallback(
    (e: React.PointerEvent) => {
      e.stopPropagation();
      onFocus?.();
      setResizing({ x: e.clientX, y: e.clientY, w, h });
    },
    [w, h, onFocus]
  );

  const style: React.CSSProperties = maximized
    ? { top: 8, left: 8, zIndex: z, width: 'calc(100% - 16px)', height: 'calc(100% - 16px)' }
    : { top: y, left: x, width: w, height: minimized ? 42 : h, zIndex: z };

  return (
    <motion.div
      ref={containerRef}
      className="absolute glass-panel overflow-hidden flex flex-col"
      style={style}
      animate={{ opacity: panel.visible ? 1 : 0, scale: minimized ? 0.95 : 1 }}
      initial={false}
      transition={{ duration: 0.25 }}
      onPointerDown={(e) => {
        onFocus?.();
        setZTop();
      }}
      onPointerMove={handlePointerMove}
      onPointerUp={handlePointerUp}
      onPointerCancel={handlePointerUp}
    >
      {panel.visible && (
        <>
          <div
            className="window-drag-handle flex items-center gap-2 px-3 py-2 bg-void-900/80 border-b border-white/10"
            style={{ boxShadow: `inset 0 1px 0 rgba(255,255,255,0.05)` }}
            onPointerDown={handlePointerDown}
          >
            <div className="flex gap-1.5">
              <button
                className="w-3 h-3 rounded-full bg-terminal-red hover:brightness-125"
                onClick={(e) => {
                  e.stopPropagation();
                  hidePanel(panelId);
                }}
                aria-label="Close"
              />
              <button
                className="w-3 h-3 rounded-full bg-terminal-amber hover:brightness-125"
                onClick={(e) => {
                  e.stopPropagation();
                  minimizePanel(panelId);
                }}
                aria-label="Minimize"
              />
              <button
                className="w-3 h-3 rounded-full bg-terminal-green hover:brightness-125"
                onClick={(e) => {
                  e.stopPropagation();
                  maximizePanel(panelId);
                }}
                aria-label="Maximize"
              />
            </div>
            <Icon size={14} className="ml-2" style={{ color: accent }} />
            <span className="text-xs font-medium text-gray-300 truncate">{title}</span>
            {panel.focused && (
              <span
                className="ml-1 h-1.5 w-1.5 rounded-full"
                style={{ background: accent, boxShadow: `0 0 6px ${accent}` }}
              />
            )}
            <div className="ml-auto flex items-center gap-1">{toolbar}</div>
          </div>

          <div className="flex-1 overflow-hidden bg-void-950/40" onPointerDown={(e) => e.stopPropagation()}>
            {minimized ? (
              <div className="h-full flex items-center justify-center text-xs text-gray-500">
                Window minimized — click in taskbar to restore
              </div>
            ) : (
              children
            )}
          </div>

          <div
            className="absolute bottom-0 right-0 w-4 h-4 cursor-nwse-resize hover:bg-virgo-500/30"
            onPointerDown={startResizing}
            aria-label="Resize"
          >
            <svg viewBox="0 0 16 16" className="w-full h-full" fill="none">
              <path d="M14 10 L10 14 M14 14 L14 14" stroke="rgba(255,255,255,0.3)" strokeWidth="1.5" />
            </svg>
          </div>
        </>
      )}
    </motion.div>
  );

  function setZTop() {
    useWorkspaceStore.getState().setZ(panelId, Date.now() % 10000);
  }
}
