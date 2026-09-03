'use client';

import { useState } from 'react';
import * as Icons from 'lucide-react';
import { FolderKanban, Plus, FileCode, Play, Trash2 } from 'lucide-react';
import { useFileStore } from '@/stores/files';

type Project = {
  name: string;
  tech: string;
  description: string;
  files: string[];
  color: string;
};

const DEFAULT_PROJECTS: Project[] = [
  {
    name: 'virgo-cloud-app',
    tech: 'Next.js · TypeScript',
    description: 'Main web application with 3D workspace',
    files: ['src/app/page.tsx', 'src/components/Scene.tsx', 'package.json'],
    color: '#3375ff',
  },
  {
    name: 'ai-backend',
    tech: 'Node.js · Express',
    description: 'Cloud sandbox & AI proxy server',
    files: ['src/index.ts', 'src/ai/AiProxy.ts', 'package.json'],
    color: '#00ff9c',
  },
  {
    name: 'data-pipeline',
    tech: 'Python · FastAPI',
    description: 'RAG memory and vector search service',
    files: ['main.py', 'rag/engine.py', 'requirements.txt'],
    color: '#b967ff',
  },
];

export function ProjectsView() {
  const [projects, setProjects] = useState(DEFAULT_PROJECTS);
  const [selected, setSelected] = useState<string | null>(null);
  const openFile = useFileStore((s) => s.openFile);

  const newProject = () => {
    const p: Project = {
      name: `project-${projects.length + 1}`,
      tech: 'TypeScript',
      description: 'New project',
      files: ['src/index.ts', 'README.md'],
      color: ['#00d4ff', '#ff5c7a', '#ffb547'][projects.length % 3],
    };
    setProjects([p, ...projects]);
    setSelected(p.name);
  };

  return (
    <div className="h-full flex flex-col bg-void-950/50">
      <div className="flex items-center gap-2 px-3 py-2 border-b border-white/5">
        <FolderKanban size={14} className="text-virgo-400" />
        <span className="text-[10px] uppercase tracking-widest text-gray-500">Projects</span>
        <button onClick={newProject} className="ml-auto p-1 rounded hover:bg-white/10" title="New project">
          <Plus size={15} className="text-virgo-300" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-3 space-y-2">
        {projects.map((p) => (
          <div
            key={p.name}
            className={`glass-panel-hover p-3 cursor-pointer transition-all ${
              selected === p.name ? 'ring-1 ring-virgo-500/50' : ''
            }`}
            onClick={() => setSelected(p.name)}
          >
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full" style={{ background: p.color }} />
              <span className="font-mono text-sm text-gray-200">{p.name}</span>
              <span className="ml-auto text-[10px] text-gray-500">{p.tech}</span>
            </div>
            <div className="mt-1 text-[11px] text-gray-500">{p.description}</div>
            {selected === p.name && (
              <div className="mt-2 pt-2 border-t border-white/5 space-y-1">
                {p.files.map((f) => (
                  <button
                    key={f}
                    onClick={(e) => {
                      e.stopPropagation();
                      openFile(`/${p.name}/${f}`);
                    }}
                    className="flex items-center gap-1.5 text-[11px] text-gray-400 hover:text-virgo-300 px-1 py-0.5 rounded hover:bg-white/5 w-full text-left"
                  >
                    <FileCode size={11} />
                    <span className="font-mono truncate">{f}</span>
                  </button>
                ))}
                <button
                  className="flex items-center gap-1.5 text-[11px] text-terminal-green hover:bg-white/5 px-1 py-0.5 rounded mt-1"
                  onClick={(e) => e.stopPropagation()}
                >
                  <Play size={11} /> Run project
                </button>
                <button
                  className="flex items-center gap-1.5 text-[11px] text-terminal-red hover:bg-white/5 px-1 py-0.5 rounded"
                  onClick={(e) => {
                    e.stopPropagation();
                    setProjects(projects.filter((x) => x.name !== p.name));
                  }}
                >
                  <Trash2 size={11} /> Delete
                </button>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
