'use client';

import { useState, useRef, useEffect } from 'react';
import * as Icons from 'lucide-react';
import { Send, Sparkles, Cpu, Brain, Zap, Bot, Settings2 } from 'lucide-react';
import { useAiStore, AI_MODELS } from '@/stores/ai';
import { api } from '@/lib/api';

export function AiChat() {
  const {
    messages,
    selectedModel,
    setSelectedModel,
    addUserMessage,
    addAssistantMessage,
    updateAssistantMessage,
    setStreaming,
    isStreaming,
    setLoading,
    loading,
    clearMessages,
  } = useAiStore();

  const [input, setInput] = useState('');
  const [showModels, setShowModels] = useState(false);
  const [backendOnline, setBackendOnline] = useState(false);
  const scrollRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' });
  }, [messages]);

  const sendQuery = async (text: string) => {
    if (!text.trim() || loading) return;
    addUserMessage(text);
    setInput('');
    setLoading(true);

    const model = AI_MODELS.find((m) => m.id === selectedModel);

    try {
      // Bound the request so a missing/hung backend can never leave the
      // send button stuck in the disabled "loading" state.
      const req = api.post<{ content: string; model: string; keyConfigured: boolean }>('/api/ai/chat', {
        model: selectedModel,
        prompt: text,
      });
      const res = await withTimeout(req, 12000);
      if (res.content) {
        setBackendOnline(true);
        const shownModel = res.model === selectedModel || !res.model ? model?.name ?? res.model : res.model;
        addAssistantMessage(res.content, shownModel);
        setLoading(false);
        return;
      }
    } catch {
      setBackendOnline(false);
      // backend unreachable — fall through to local
    }

    // Local fallback (backend offline)
    const reply = generateReply(text);
    addAssistantMessage(reply, `${model?.name ?? 'auto'}${offlineLabel()}`);
    setLoading(false);
  };

  const offlineLabel = () => (backendOnline ? '' : ' (offline)');

  function withTimeout<T>(promise: Promise<T>, ms: number): Promise<T> {
    return Promise.race([
      promise,
      new Promise<never>((_, reject) => setTimeout(() => reject(new Error('timeout')), ms)),
    ]);
  }

  const generateReply = (prompt: string): string => {
    const p = prompt.toLowerCase();
    if (p.includes('hello') || p.includes('hi') || p.includes('hey')) {
      return `👋 **Hello!**\n\nI'm your **VirgoYT AI assistant**. I'm online with the **${currentModelName()}** model.\n\nI can:\n• 💻 Generate full applications\n• 🛠️ Write & edit code files\n• 🔧 Explain and debug anything\n• 📦 Manage your cloud workspace\n• 🌐 Browse & test websites\n\nWhat are we building today?`;
    }
    if (p.includes('terminal') || p.includes('command') || p.includes('run')) {
      return `Here's a **terminal command** you can use:\n\n\`\`\`bash\n# Run your project\nyour command here\n\`\`\`\n\nOpen the **Terminal** panel to execute it in your cloud sandbox. The sandbox runs a real Linux environment with Node.js, Python, Git, and more — you can install packages, compile code, and run servers.`;
    }
    if (p.includes('build') || p.includes('make') || p.includes('create app')) {
      return `Let's build that! 🚀\n\nHere's my **development plan**:\n\n1. **Scaffold the project** — I'll set up the structure in your workspace\n2. **Write the core files** — CSS, HTML, logic\n3. **Test & run** — Launch it in the browser sandbox\n4. **Deploy** — Publish it live to GitHub Pages\n\nDrop your full requirements and I'll start writing the code. I work through a **multi-agent swarm** (Architect → Coder → Reviewer → QA → DevOps).`;
    }
    if (p.includes('file') || p.includes('create file') || p.includes('write')) {
      return `I can create that file in your workspace. 🗂️\n\nOpen the **File Explorer** panel, and ask me for the exact content you need. Or just tell me *what* the file should do and I'll generate production-ready code.\n\n**Example request:**\n> Create a \`server.py\` file that runs an HTTP server with a health check endpoint`;
    }
    if (p.includes('who are you') || p.includes('your name') || p.includes('virgoyt')) {
      return `⚡ I'm **VirgoYT Cloud AI** — an autonomous cloud software engineer.\n\nI live in a **cloud sandbox** where I can:\n\n- 🖥️ Run a real **Linux terminal**\n- 📝 Write & edit **code files**\n- 🌐 Open a **browser** to test apps\n- 📊 Monitor **system resources**\n- 🤖 Delegate tasks to a **multi-agent team**\n\nThink of me as your personal AI developer + cloud computer, running right in your browser.`;
    }
    if (p.includes('deploy') || p.includes('publish') || p.includes('github')) {
      return `Here's the **deployment pipeline** you can use:\n\n\`\`\`bash\n# From your cloud terminal\ngit add .\ngit commit -m "feat: my app"\ngit push origin main\n\`\`\`\n\nOnce pushed, your **GitHub Actions** workflow auto-builds and deploys to **GitHub Pages** — giving you a live URL in minutes. I can also configure CI/CD for you.`;
    }
    if (p.includes('error') || p.includes('bug') || p.includes('fix')) {
      return `Let's **debug that** together. 🔍\n\n1. **Paste the error** and I'll analyze the root cause\n2. I'll check your **workspace files** for context\n3. I'll propose a **fix** and apply it to the editor\n\nShare the error output from the terminal and I'll get right on it.`;
    }
    return `**Here's how I can help with that:**\n\nI'm ready to work in your cloud environment. Here's what I need from you:\n\n1. **Be specific** — What exactly do you want to build or do?\n2. **I'll take action** — Write files, run commands, browse, and test.\n3. **Watch it live** — All changes happen in real-time in your workspace panels.\n\n**Quick things you can ask me:**\n> ● Create a React todo app\n> ● Set up a Python FastAPI server\n> ● Write a bash script to automate X\n> ● Explain this error message\n\nWhat shall we work on?`;
  };

  const currentModelName = () => AI_MODELS.find((m) => m.id === selectedModel)?.name ?? 'Auto Router';

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendQuery(input);
    }
  };

  return (
    <div className="h-full flex flex-col bg-void-950/50">
      <div className="flex items-center gap-2 px-3 py-2 border-b border-white/5">
        <Bot size={14} className="text-virgo-400" />
        <span className="text-[10px] uppercase tracking-widest text-gray-500">AI Assistant</span>
        <div className="relative ml-auto">
          <button
            className="flex items-center gap-1.5 text-[11px] px-2 py-1 rounded-md bg-white/5 hover:bg-white/10 border border-white/10"
            onClick={() => setShowModels((s) => !s)}
          >
            <Brain size={12} className="text-virgo-300" />
            <span className="text-gray-300">{currentModelName()}</span>
            <Settings2 size={11} className="text-gray-500" />
          </button>
          {showModels && (
            <div className="absolute right-0 top-8 w-56 glass-panel p-1 z-50">
              {AI_MODELS.map((m) => (
                <button
                  key={m.id}
                  onClick={() => {
                    setSelectedModel(m.id);
                    setShowModels(false);
                  }}
                  className={`w-full text-left px-2 py-1.5 rounded text-xs hover:bg-white/10 ${
                    m.id === selectedModel ? 'bg-virgo-500/20 text-virgo-200' : 'text-gray-300'
                  }`}
                >
                  <div className="flex items-center gap-2">
                    <Zap size={12} className={m.id === selectedModel ? 'text-virgo-300' : 'text-gray-500'} />
                    <span>{m.name}</span>
                  </div>
                  <div className="text-[9px] text-gray-500 pl-6">{m.provider}</div>
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      <div ref={scrollRef} className="flex-1 overflow-y-auto px-4 py-4 space-y-4">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
          >
            <div
              className={`max-w-[85%] px-3 py-2 rounded-xl text-[13px] leading-relaxed ${
                msg.role === 'user'
                  ? 'bg-virgo-600/80 text-white rounded-br-sm'
                  : 'bg-white/5 text-gray-200 rounded-bl-sm border border-white/5'
              }`}
            >
              {msg.role === 'assistant' && (
                <div className="flex items-center gap-1.5 mb-1.5">
                  <Sparkles size={11} className="text-virgo-300" />
                  <span className="text-[9px] uppercase tracking-wider text-gray-500">
                    {msg.model ?? 'virgoyt'} {msg.streaming && '...'}
                  </span>
                </div>
              )}
              <div className="whitespace-pre-wrap">{msg.content}</div>
            </div>
          </div>
        ))}
        {loading && (
          <div className="flex items-center gap-2 text-xs text-gray-500">
            <Cpu size={14} className="animate-spin text-virgo-400" />
            <span>VirgoYT thinking...</span>
            <span className="flex gap-0.5">
              <span className="w-1 h-1 rounded-full bg-virgo-400 animate-bounce" />
              <span className="w-1 h-1 rounded-full bg-virgo-400 animate-bounce [animation-delay:0.1s]" />
              <span className="w-1 h-1 rounded-full bg-virgo-400 animate-bounce [animation-delay:0.2s]" />
            </span>
          </div>
        )}
      </div>

      <div className="px-3 pb-3 pt-1">
        <div className="flex items-end gap-2 glass-panel p-2">
          <textarea
            ref={inputRef}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Ask VirgoYT to build something..."
            rows={1}
            className="flex-1 bg-transparent resize-none outline-none text-sm text-gray-200 placeholder-gray-600 max-h-32"
          />
          <button
            onClick={() => sendQuery(input)}
            disabled={loading}
            className="p-2 rounded-lg bg-virgo-600 hover:bg-virgo-500 disabled:opacity-40 transition-colors flex items-center justify-center"
            aria-label="Send"
          >
            <Send size={15} className="text-white" />
          </button>
        </div>
        <div className="flex gap-1.5 mt-2">
          {['Create a todo app', 'Explain the terminal', 'Deploy my project'].map((suggestion) => (
            <button
              key={suggestion}
              onClick={() => sendQuery(suggestion)}
              className="text-[10px] px-2 py-1 rounded-full bg-white/5 border border-white/10 text-gray-400 hover:bg-virgo-500/20 hover:text-virgo-200 transition-colors"
            >
              {suggestion}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
