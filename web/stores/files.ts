import { create } from 'zustand';

export type FileNode = {
  name: string;
  path: string;
  type: 'file' | 'directory';
  children?: FileNode[];
  modified?: boolean;
};

export type CodeFile = {
  path: string;
  content: string;
  language: string;
  saved: boolean;
};

type FileState = {
  fileTree: FileNode[];
  files: Record<string, CodeFile>;
  activeFilePath: string | null;
  openTabs: string[];
  currentDir: string;
  setFileTree: (tree: FileNode[]) => void;
  setFiles: (files: Record<string, CodeFile>) => void;
  openFile: (path: string, content?: string) => void;
  closeFile: (path: string) => void;
  updateContent: (path: string, content: string) => void;
  markSaved: (path: string) => void;
  setActiveFile: (path: string | null) => void;
  setCurrentDir: (dir: string) => void;
  markModified: (path: string) => void;
};

const languageFromPath = (path: string): string => {
  const ext = path.split('.').pop()?.toLowerCase();
  const map: Record<string, string> = {
    ts: 'typescript',
    tsx: 'typescript',
    js: 'javascript',
    jsx: 'javascript',
    py: 'python',
    rb: 'ruby',
    go: 'go',
    rs: 'rust',
    java: 'java',
    kt: 'kotlin',
    c: 'c',
    cpp: 'cpp',
    h: 'cpp',
    cs: 'csharp',
    html: 'html',
    css: 'css',
    scss: 'scss',
    json: 'json',
    md: 'markdown',
    yml: 'yaml',
    yaml: 'yaml',
    xml: 'xml',
    sh: 'shell',
    bash: 'shell',
    sql: 'sql',
    php: 'php',
    vue: 'vue',
    dart: 'dart',
    swift: 'swift',
    txt: 'plaintext',
  };
  return map[ext ?? ''] ?? 'plaintext';
};

export const useFileStore = create<FileState>((set, get) => ({
  fileTree: [],
  files: {},
  activeFilePath: null,
  openTabs: [],
  currentDir: '/workspace',

  setFileTree: (fileTree) => set({ fileTree }),

  setFiles: (files) => set({ files }),

  openFile: (path, content) =>
    set((s) => {
      const existing = s.files[path];
      const newContent = content ?? existing?.content ?? '';
      const file: CodeFile = {
        path,
        content: newContent,
        language: languageFromPath(path),
        saved: true,
      };
      const openTabs = s.openTabs.includes(path) ? s.openTabs : [...s.openTabs, path];
      return {
        files: { ...s.files, [path]: file },
        openTabs,
        activeFilePath: path,
      };
    }),

  closeFile: (path) =>
    set((s) => {
      const openTabs = s.openTabs.filter((p) => p !== path);
      const files = { ...s.files };
      delete files[path];
      let activeFilePath = s.activeFilePath;
      if (activeFilePath === path) {
        activeFilePath = openTabs[openTabs.length - 1] ?? null;
      }
      return { openTabs, files, activeFilePath };
    }),

  updateContent: (path, content) =>
    set((s) => ({
      files: {
        ...s.files,
        [path]: { ...s.files[path], content, saved: false, modified: true },
      },
    })),

  markSaved: (path) =>
    set((s) => ({
      files: { ...s.files, [path]: { ...s.files[path], saved: true } },
    })),

  setActiveFile: (activeFilePath) => set({ activeFilePath }),

  setCurrentDir: (currentDir) => set({ currentDir }),

  markModified: (path) =>
    set((s) => ({
      files: { ...s.files, [path]: { ...s.files[path], modified: true } },
    })),
}));
