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
  { id: 'auto', name: 'Auto Router', provider: 'VirgoYT', description: 'Intelligently routes to the best model for each task across all connected providers', capabilities: ['auto'] },
  { id: 'gemini-flash', name: 'Gemini 2.5 Flash', provider: 'Google', description: 'Fast, efficient, free tier available', capabilities: ['code', 'chat', 'fast', 'vision'] },
  { id: 'gemini-pro', name: 'Gemini 2.5 Pro', provider: 'Google', description: 'Powerful reasoning for complex problems', capabilities: ['code', 'chat', 'reasoning', 'long', 'vision'] },
  { id: 'gpt-4o', name: 'GPT-4o', provider: 'OpenAI', description: 'Multimodal flagship, strong coding', capabilities: ['code', 'chat', 'vision', 'reasoning'] },
  { id: 'gpt-4o-mini', name: 'GPT-4o mini', provider: 'OpenAI', description: 'Fast, affordable OpenAI model', capabilities: ['code', 'chat', 'fast', 'vision'] },
  { id: 'o3-mini', name: 'o3-mini', provider: 'OpenAI', description: 'Deep reasoning for science & code', capabilities: ['code', 'reasoning', 'math'] },
  { id: 'claude-sonnet', name: 'Claude 3.7 Sonnet', provider: 'Anthropic', description: 'Hybrid standard & extended thinking', capabilities: ['code', 'chat', 'reasoning', 'vision'] },
  { id: 'claude-haiku', name: 'Claude 3.5 Haiku', provider: 'Anthropic', description: 'Fast & lightweight Claude model', capabilities: ['code', 'chat', 'fast', 'vision'] },
  { id: 'deepseek-r1', name: 'DeepSeek R1', provider: 'DeepSeek / Kie.ai', description: 'Open-weights chain-of-thought reasoning', capabilities: ['code', 'reasoning', 'math'] },
  { id: 'deepseek-v3', name: 'DeepSeek V3', provider: 'DeepSeek / Kie.ai', description: 'Fast 671B MoE general intelligence', capabilities: ['code', 'chat', 'fast'] },
  { id: 'openrouter', name: 'OpenRouter (200+ models)', provider: 'OpenRouter', description: 'Route to 200+ open & closed models', capabilities: ['code', 'chat', 'vision', 'reasoning'] },
  { id: 'qwen-coder', name: 'Qwen 2.5 Coder 32B', provider: 'Alibaba', description: 'Specialized competitive coding', capabilities: ['code'] },
  { id: 'groq-llama', name: 'Llama 3.3 70B (Groq)', provider: 'Meta / Groq', description: 'Sub-second LPU inference', capabilities: ['code', 'chat', 'fast'] },
  { id: 'groq-fast', name: 'Llama 3.1 8B (Groq FREE)', provider: 'Meta / Groq', description: 'Free tier, very fast', capabilities: ['chat', 'fast', 'free'] },
  { id: 'nvidia-llama', name: 'Llama 3.3 70B (NVIDIA NIM)', provider: 'NVIDIA', description: 'Enterprise GPU inference', capabilities: ['code', 'chat'] },
  { id: 'mistral-large', name: 'Mistral Large 2', provider: 'Mistral AI', description: 'European multilingual flagship', capabilities: ['code', 'chat', 'reasoning'] },
  { id: 'bazaarlink', name: 'BazaarLink Universal', provider: 'BazaarLink.ai', description: 'Single endpoint to all frontier models', capabilities: ['code', 'chat', 'vision'] },
  { id: 'hf-codellama', name: 'CodeLlama 34B (HF FREE)', provider: 'Hugging Face', description: 'Free open-source coding model', capabilities: ['code', 'free'] },
  { id: 'hf-llama', name: 'Llama 3.2 3B (HF FREE)', provider: 'Hugging Face', description: 'Free lightweight inference', capabilities: ['chat', 'fast', 'free'] },
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
