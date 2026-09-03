import { create } from 'zustand';

export type ChatRole = 'user' | 'assistant' | 'system';

export type ChatMessage = {
  id: string;
  role: ChatRole;
  content: string;
  timestamp: number;
  model?: string;
  streaming?: boolean;
};

export type AiModel = {
  id: string;
  name: string;
  provider: string;
  description: string;
  capabilities: string[];
};

export const AI_MODELS: AiModel[] = [
  {
    id: 'auto',
    name: 'Auto Router',
    provider: 'VirgoYT',
    description: 'Intelligently routes to the best model for each task',
    capabilities: ['auto'],
  },
  {
    id: 'gemini-flash',
    name: 'Gemini 2.5 Flash',
    provider: 'Google',
    description: 'Fast, efficient for everyday coding tasks',
    capabilities: ['code', 'chat', 'fast'],
  },
  {
    id: 'gemini-pro',
    name: 'Gemini 2.5 Pro',
    provider: 'Google',
    description: 'Powerful reasoning for complex problems',
    capabilities: ['code', 'chat', 'reasoning', 'long'],
  },
  {
    id: 'claude-sonnet',
    name: 'Claude 3.5 Sonnet',
    provider: 'Anthropic',
    description: 'Balanced performance and safety',
    capabilities: ['code', 'chat', 'reasoning'],
  },
  {
    id: 'deepseek',
    name: 'DeepSeek R1',
    provider: 'DeepSeek',
    description: 'Open-source reasoning powerhouse',
    capabilities: ['code', 'reasoning', 'math'],
  },
  {
    id: 'qwen',
    name: 'Qwen 2.5 Coder',
    provider: 'Alibaba',
    description: 'Specialized code generation model',
    capabilities: ['code'],
  },
];

type AiState = {
  messages: ChatMessage[];
  selectedModel: string;
  isStreaming: boolean;
  contextFiles: string[];
  loading: boolean;
  setSelectedModel: (model: string) => void;
  addUserMessage: (content: string) => void;
  addAssistantMessage: (content: string, model?: string) => void;
  updateAssistantMessage: (id: string, content: string) => void;
  setStreaming: (streaming: boolean) => void;
  setLoading: (loading: boolean) => void;
  clearMessages: () => void;
  addContextFile: (path: string) => void;
  removeContextFile: (path: string) => void;
  clearContextFiles: () => void;
};

export const useAiStore = create<AiState>((set) => ({
  messages: [
    {
      id: 'welcome',
      role: 'assistant',
      content:
        '⚡ **VirgoYT AI Assistant online.**\n\nI can help you build apps directly in your cloud environment. Ask me to write code, explain anything, create files, run commands, or debug issues.\n\nAvailable tools: code editor, terminal, file manager, browser sandbox, AI agent swarm.',
      timestamp: Date.now(),
      model: 'auto',
    },
  ],
  selectedModel: 'auto',
  isStreaming: false,
  contextFiles: [],
  loading: false,

  setSelectedModel: (selectedModel) => set({ selectedModel }),

  addUserMessage: (content) =>
    set((s) => ({
      messages: [
        ...s.messages,
        {
          id: `u-${Date.now()}`,
          role: 'user',
          content,
          timestamp: Date.now(),
        },
      ],
    })),

  addAssistantMessage: (content, model) =>
    set((s) => ({
      messages: [
        ...s.messages,
        {
          id: `a-${Date.now()}`,
          role: 'assistant',
          content,
          timestamp: Date.now(),
          model: model ?? s.selectedModel,
        },
      ],
      loading: false,
    })),

  updateAssistantMessage: (id, content) =>
    set((s) => ({
      messages: s.messages.map((m) =>
        m.id === id ? { ...m, content, streaming: true } : m
      ),
    })),

  setStreaming: (isStreaming) => set({ isStreaming }),

  setLoading: (loading) => set({ loading }),

  clearMessages: () =>
    set({
      messages: [
        {
          id: 'welcome',
          role: 'assistant',
          content:
            '⚡ **VirgoYT AI Assistant online.**\n\nChat history cleared. Ask me anything to start building.',
          timestamp: Date.now(),
          model: 'auto',
        },
      ],
    }),

  addContextFile: (path) =>
    set((s) => {
      if (s.contextFiles.includes(path)) return s;
      return { contextFiles: [...s.contextFiles, path] };
    }),

  removeContextFile: (path) =>
    set((s) => ({
      contextFiles: s.contextFiles.filter((p) => p !== path),
    })),

  clearContextFiles: () => set({ contextFiles: [] }),
}));
