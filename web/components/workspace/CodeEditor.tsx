'use client';

import { useEffect, useRef, useState } from 'react';
import dynamic from 'next/dynamic';

const MonacoEditor = dynamic(() => import('@monaco-editor/react'), {
  ssr: false,
  loading: () => <div className="flex items-center justify-center h-full text-sm text-gray-500">Loading editor...</div>,
});

type CodeEditorProps = {
  path: string;
  value: string;
  language: string;
  onChange?: (value: string) => void;
  readOnly?: boolean;
};

export function CodeEditor({ path, value, language = 'plaintext', onChange, readOnly }: CodeEditorProps) {
  const mounted = useRef(true);

  return (
    <div className="w-full h-full">
      <MonacoEditor
        height="100%"
        language={language}
        value={value}
        onChange={(v) => onChange?.(v ?? '')}
        theme="vs-dark"
        options={{
          fontSize: 13,
          fontFamily: '"JetBrains Mono", ui-monospace, monospace',
          minimap: { enabled: true },
          scrollBeyondLastLine: false,
          automaticLayout: true,
          readOnly,
          wordWrap: 'on',
          smoothScrolling: true,
          cursorBlinking: 'smooth',
          cursorSmoothCaretAnimation: 'on',
          padding: { top: 12, bottom: 12 },
          tabSize: 2,
          renderWhitespace: 'selection',
          bracketPairColorization: { enabled: true },
          guides: { bracketPairs: true },
        }}
        loading={<div className="flex items-center justify-center h-full text-sm text-gray-500">Booting editor...</div>}
      />
    </div>
  );
}
