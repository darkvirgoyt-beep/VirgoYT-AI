'use client';

import { useEffect, useState } from 'react';
import dynamic from 'next/dynamic';
import { motion, AnimatePresence } from 'framer-motion';
import * as Icons from 'lucide-react';
import { Window } from '@/components/workspace/Window';
import { Taskbar } from '@/components/workspace/Taskbar';
import { TopBar } from '@/components/workspace/TopBar';
import { CodeEditor } from '@/components/workspace/CodeEditor';
import { TerminalView } from '@/components/workspace/Terminal';
import { FileManager } from '@/components/workspace/FileManager';
import { AiChat } from '@/components/workspace/AiChat';
import { AgentPanel } from '@/components/workspace/AgentPanel';
import { BrowserSandbox } from '@/components/workspace/BrowserSandbox';
import { DesktopComputer } from '@/components/workspace/DesktopComputer';
import { SystemMonitor } from '@/components/workspace/SystemMonitor';
import WelcomeOverlay from '@/components/workspace/WelcomeOverlay';
import { useWorkspaceStore } from '@/stores/workspace';
import { useFileStore } from '@/stores/files';
import { useTerminalStore } from '@/stores/terminal';
import { useSystemStore } from '@/stores/system';

const Scene3D = dynamic(
  () => import('@/components/canvas/Scene3D').then((m) => m.Scene3D),
  { ssr: false, loading: () => null }
);

export function WorkspaceClient() {
  const setConnected = useSystemStore((s) => s.setConnected);
  const activeFilePath = useFileStore((s) => s.activeFilePath);
  const files = useFileStore((s) => s.files);
  const openTabs = useFileStore((s) => s.openTabs);
  const updateContent = useFileStore((s) => s.updateContent);
  const setActiveFile = useFileStore((s) => s.setActiveFile);
  const closeFile = useFileStore((s) => s.closeFile);

  const [ready, setReady] = useState(false);
  const [showWelcome, setShowWelcome] = useState(false);

  const activeFile = activeFilePath ? files[activeFilePath] : null;

  useEffect(() => {
    const t = setTimeout(() => {
      setConnected(true);
      setReady(true);
    }, 500);
    return () => clearTimeout(t);
  }, [setConnected]);

  const editorToolbar =
    activeFile && !activeFile.saved ? (
      <span className="text-[10px] text-amber-400">● unsaved</span>
    ) : null;

  return (
    <div className="relative w-screen h-screen bg-void-950 select-none max-md:overflow-auto overflow-hidden">
      {/* 3D background */}
      <Scene3D onReady={() => setShowWelcome(true)} />

      {/* Top status bar */}
      <TopBar />

      {/* Workspace panels */}
      <div className="absolute inset-0 z-10">
        <Window panelId="editor" title="Code Editor" icon="Code2" accent="#3375ff" toolbar={editorToolbar}>
          <EditorTabs
            openTabs={openTabs}
            activeFilePath={activeFilePath}
            files={files}
            onSelect={setActiveFile}
            onClose={closeFile}
          />
          {activeFile ? (
            <CodeEditor
              key={activeFile.path}
              path={activeFile.path}
              language={activeFile.language}
              value={activeFile.content}
              onChange={(v) => updateContent(activeFile.path, v)}
            />
          ) : (
            <div className="h-full flex flex-col items-center justify-center text-gray-500 gap-2">
              <span className="text-5xl">📁</span>
              <span className="text-sm">Open a file from the Explorer to start editing</span>
            </div>
          )}
        </Window>

        <Window panelId="terminal" title="Terminal" icon="TerminalSquare" accent="#00ff9c">
          <TerminalHost />
        </Window>

        <Window panelId="files" title="Files" icon="FolderTree" accent="#ffb547">
          <FileManager />
        </Window>

        <Window panelId="ai" title="AI Assistant" icon="Sparkles" accent="#b967ff">
          <AiChat />
        </Window>

        <Window panelId="agent" title="Virgo Agent" icon="Bot" accent="#00e5ff">
          <AgentPanel />
        </Window>

        <Window panelId="browser" title="Browser Sandbox" icon="Globe" accent="#00d4ff">
          <BrowserSandbox />
        </Window>

        <Window panelId="computer" title="VirgoYT Computer" icon="Monitor" accent="#00e5ff">
          <DesktopComputer />
        </Window>

        <Window panelId="monitor" title="System Monitor" icon="Activity" accent="#ff5c7a">
          <SystemMonitor />
        </Window>
      </div>

      {/* Dock */}
      <Taskbar />

      {/* Welcome overlay */}
      <AnimatePresence>
        {showWelcome && <WelcomeOverlay onEnter={() => setShowWelcome(false)} />}
      </AnimatePresence>

      {/* Loading screen */}
      <AnimatePresence>
        {!ready && (
          <motion.div
            className="absolute inset-0 z-[10000] flex flex-col items-center justify-center bg-void-950"
            exit={{ opacity: 0 }}
            transition={{ duration: 0.6 }}
          >
            <div className="w-20 h-20 border-4 border-virgo-500 border-t-transparent rounded-full animate-spin" />
            <div className="mt-6 text-sm text-virgo-300 tracking-widest uppercase">VirgoYT Cloud AI</div>
            <div className="mt-2 text-xs text-gray-500">Initializing 3D environment...</div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

function EditorTabs({
  openTabs,
  activeFilePath,
  files,
  onSelect,
  onClose,
}: {
  openTabs: string[];
  activeFilePath: string | null;
  files: Record<string, any>;
  onSelect: (p: string) => void;
  onClose: (p: string) => void;
}) {
  return (
    <div className="flex items-center gap-0.5 bg-void-900/80 border-b border-white/5 px-2 overflow-x-auto no-scrollbar">
      {openTabs.map((path) => {
        const f = files[path];
        const active = path === activeFilePath;
        return (
          <div
            key={path}
            onClick={() => onSelect(path)}
            className={`flex items-center gap-1.5 px-3 py-1.5 text-xs cursor-pointer border-t-2 transition-colors whitespace-nowrap ${
              active
                ? 'bg-white/5 text-gray-200 border-virgo-400'
                : 'text-gray-500 border-transparent hover:text-gray-300'
            }`}
          >
            <span>{path.split('/').pop()}</span>
            {f?.saved === false && <span className="w-1.5 h-1.5 rounded-full bg-amber-400" />}
            <button
              onClick={(e) => {
                e.stopPropagation();
                onClose(path);
              }}
              className="hover:bg-white/10 rounded p-0.5"
            >
              <Icons.X size={11} />
            </button>
          </div>
        );
      })}
    </div>
  );
}

function TerminalHost() {
  const activeTabId = useTerminalStore((s) => s.activeTabId);
  const tabs = useTerminalStore((s) => s.tabs);
  const setActiveTab = useTerminalStore((s) => s.setActiveTab);
  const addTab = useTerminalStore((s) => s.addTab);
  const removeTab = useTerminalStore((s) => s.removeTab);

  return (
    <div className="h-full flex flex-col">
      <div className="flex items-center gap-1 px-2 bg-void-900/80 border-b border-white/5 overflow-x-auto no-scrollbar">
        {tabs.map((tab) => (
          <div
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center gap-1 px-2 py-1 text-[11px] cursor-pointer border-b-2 transition-colors whitespace-nowrap ${
              tab.id === activeTabId ? 'text-gray-200 border-terminal-green/70' : 'text-gray-500 border-transparent'
            }`}
          >
            <span className="w-1.5 h-1.5 rounded-full" style={{ background: tab.color }} />
            {tab.name}
            <button
              onClick={(e) => {
                e.stopPropagation();
                removeTab(tab.id);
              }}
              className="hover:bg-white/10 rounded p-px"
            >
              <Icons.X size={10} />
            </button>
          </div>
        ))}
        <button onClick={addTab} className="ml-auto p-1 text-gray-500 hover:text-gray-300">
          <Icons.Plus size={12} />
        </button>
      </div>
      <div className="flex-1">
        <TerminalView key={activeTabId} tabId={activeTabId} />
      </div>
    </div>
  );
}
