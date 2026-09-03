'use client';

import { useEffect, useState } from 'react';
import * as Icons from 'lucide-react';
import { Folder, File, ChevronRight, ChevronDown } from 'lucide-react';
import { useFileStore, FileNode } from '@/stores/files';
import { api } from '@/lib/api';

export function FileManager() {
  const { fileTree, setFileTree, openFile, currentDir, setCurrentDir } = useFileStore();
  const [expanded, setExpanded] = useState<Set<string>>(new Set(['/workspace']));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTree();
  }, [currentDir]);

  const loadTree = async () => {
    setLoading(true);
    try {
      const res = await api.get<{ tree: FileNode[] }>('/files/tree?path=' + encodeURIComponent(currentDir));
      setFileTree(res.tree);
    } catch {
      setFileTree(demoTree());
    }
    setLoading(false);
  };

  const demoTree = (): FileNode[] => [
    {
      name: 'workspace',
      path: '/workspace',
      type: 'directory',
      children: [
        { name: 'src', path: '/workspace/src', type: 'directory', children: [
          { name: 'index.ts', path: '/workspace/src/index.ts', type: 'file' },
          { name: 'app.ts', path: '/workspace/src/app.ts', type: 'file' },
          { name: 'utils.ts', path: '/workspace/src/utils.ts', type: 'file' },
        ]},
        { name: 'package.json', path: '/workspace/package.json', type: 'file' },
        { name: 'README.md', path: '/workspace/README.md', type: 'file' },
        { name: 'tsconfig.json', path: '/workspace/tsconfig.json', type: 'file' },
        { name: '.gitignore', path: '/workspace/.gitignore', type: 'file' },
      ],
    },
  ];

  const toggleExpand = (path: string) => {
    setExpanded((prev) => {
      const next = new Set(prev);
      if (next.has(path)) next.delete(path);
      else next.add(path);
      return next;
    });
  };

  const handleFileClick = async (path: string) => {
    try {
      const res = await api.get<{ content: string }>('/files/read?path=' + encodeURIComponent(path));
      openFile(path, res.content);
    } catch {
      openFile(path, demoContent(path));
    }
  };

  const demoContent = (path: string): string => {
    if (path.endsWith('.json')) return '{\n  "name": "workspace",\n  "version": "1.0.0"\n}\n';
    if (path.endsWith('.md'))
      return '# Workspace\n\nWelcome to your cloud environment.\n\n## Quick Start\n\n```bash\nnpm run dev\n```\n';
    if (path.endsWith('.ts'))
      return 'import { App } from "./app";\n\nconst app = new App();\napp.start();\nconsole.log("Hello from VirgoYT Cloud");\n';
    return `// ${path}\n\n// This file was created in your cloud workspace.\n`;
  };

  const renderNode = (node: FileNode, depth: number) => {
    const isDir = node.type === 'directory';
    const isExpanded = expanded.has(node.path);
    const IconComp = isDir ? (isExpanded ? ChevronDown : ChevronRight) : null;

    return (
      <div key={node.path}>
        <div
          className="flex items-center gap-1.5 px-2 py-1 text-[13px] cursor-pointer hover:bg-white/5 rounded transition-colors"
          style={{ paddingLeft: `${8 + depth * 14}px` }}
          onClick={() => {
            if (isDir) {
              toggleExpand(node.path);
              setCurrentDir(node.path);
            } else {
              handleFileClick(node.path);
            }
          }}
        >
          {IconComp ? <IconComp size={12} className="text-gray-500" /> : <span className="w-3" />}
          {isDir ? <Folder size={14} className="text-virgo-400" /> : <File size={14} className="text-gray-400" />}
          <span className={isDir ? 'text-gray-200' : 'text-gray-400'}>{node.name}</span>
        </div>
        {isDir && isExpanded && node.children?.map((child) => renderNode(child, depth + 1))}
      </div>
    );
  };

  return (
    <div className="h-full flex flex-col bg-void-950/50">
      <div className="flex items-center gap-2 px-3 py-2 border-b border-white/5">
        <span className="text-[10px] uppercase tracking-widest text-gray-500">Explorer</span>
        <button
          className="ml-auto p-1 rounded hover:bg-white/10"
          onClick={loadTree}
          title="Refresh"
        >
          <Icons.RefreshCw size={13} className="text-gray-400" />
        </button>
      </div>
      <div className="flex-1 overflow-auto py-1">
        {loading ? (
          <div className="p-4 text-xs text-gray-500">Loading workspace...</div>
        ) : (
          fileTree.map((node) => renderNode(node, 0))
        )}
      </div>
    </div>
  );
}
