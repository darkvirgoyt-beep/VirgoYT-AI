// VirgoYT multi-provider AI gateway.
// Unified access to Gemini, OpenAI, Anthropic Claude, Groq, OpenRouter, DeepSeek (Kie),
// NVIDIA NIM, Mistral, BazaarLink, Qwen and Hugging Face.
// All except Gemini/HF use the OpenAI-compatible /v1/chat/completions format.

export type ChatHistoryItem = { role: string; content: string };

export type GatewayRequest = {
  model: string; // model id from the registry, or explicit override
  prompt: string;
  system?: string;
  history?: ChatHistoryItem[];
  temperature?: number;
  maxTokens?: number;
  stream?: boolean; // reserved for future streaming
};

export type ProviderModel = {
  id: string; // gateway model id
  name: string;
  provider: string;
  providerKey: keyof typeof env;
  endpoint: string;
  modelParam: string;
  defaultModelParam: string;
  free?: boolean;
  vision?: boolean;
  reasoning?: boolean;
  context?: number;
};

// Map of every provider's OpenAI-compatible/HTTP endpoint
const PROVIDER_ENDPOINTS: Record<string, string> = {
  gemini: 'https://generativelanguage.googleapis.com/v1beta',
  openai: 'https://api.openai.com/v1',
  anthropic: 'https://api.anthropic.com/v1',
  groq: 'https://api.groq.com/openai/v1',
  openrouter: 'https://openrouter.ai/api/v1',
  kie: 'https://api.kie.ai/v1',
  nvidia: 'https://integrate.api.nvidia.com/v1',
  mistral: 'https://api.mistral.ai/v1',
  bazaarlink: 'https://api.bazaarlink.ai/v1',
  huggingface: 'https://api-inference.huggingface.co',
  qwen: 'https://openrouter.ai/api/v1',
};

const MODEL_REGISTRY: ProviderModel[] = [
  // Google Gemini
  { id: 'gemini-flash', name: 'Gemini 2.5 Flash', provider: 'Google DeepMind', providerKey: 'GEMINI_API_KEY', endpoint: PROVIDER_ENDPOINTS.gemini, modelParam: 'models/gemini-2.5-flash', defaultModelParam: 'gemini-2.5-flash', vision: true, context: 1000000 },
  { id: 'gemini-pro', name: 'Gemini 2.5 Pro', provider: 'Google DeepMind', providerKey: 'GEMINI_API_KEY', endpoint: PROVIDER_ENDPOINTS.gemini, modelParam: 'models/gemini-2.5-pro', defaultModelParam: 'gemini-2.5-pro', vision: true, reasoning: true, context: 2000000 },

  // OpenAI / ChatGPT
  { id: 'gpt-4o', name: 'GPT-4o', provider: 'OpenAI', providerKey: 'OPENAI_API_KEY', endpoint: PROVIDER_ENDPOINTS.openai, modelParam: 'gpt-4o', defaultModelParam: 'gpt-4o', vision: true, context: 128000 },
  { id: 'gpt-4o-mini', name: 'GPT-4o mini', provider: 'OpenAI', providerKey: 'OPENAI_API_KEY', endpoint: PROVIDER_ENDPOINTS.openai, modelParam: 'gpt-4o-mini', defaultModelParam: 'gpt-4o-mini', vision: true, context: 128000 },
  { id: 'o3-mini', name: 'o3-mini', provider: 'OpenAI', providerKey: 'OPENAI_API_KEY', endpoint: PROVIDER_ENDPOINTS.openai, modelParam: 'o3-mini', defaultModelParam: 'o3-mini', reasoning: true, context: 200000 },

  // Anthropic Claude
  { id: 'claude-sonnet', name: 'Claude 3.7 Sonnet', provider: 'Anthropic', providerKey: 'ANTHROPIC_API_KEY', endpoint: PROVIDER_ENDPOINTS.anthropic, modelParam: 'claude-3-7-sonnet-20250219', defaultModelParam: 'claude-3-7-sonnet-20250219', vision: true, reasoning: true, context: 200000 },
  { id: 'claude-haiku', name: 'Claude 3.5 Haiku', provider: 'Anthropic', providerKey: 'ANTHROPIC_API_KEY', endpoint: PROVIDER_ENDPOINTS.anthropic, modelParam: 'claude-3-5-haiku-20241022', defaultModelParam: 'claude-3-5-haiku-20241022', vision: true, context: 200000 },

  // DeepSeek (via Kie.ai)
  { id: 'deepseek-r1', name: 'DeepSeek R1', provider: 'DeepSeek / Kie.ai', providerKey: 'KIE_API_KEY', endpoint: PROVIDER_ENDPOINTS.kie, modelParam: 'deepseek-reasoner', defaultModelParam: 'deepseek-reasoner', reasoning: true, context: 128000 },
  { id: 'deepseek-v3', name: 'DeepSeek V3', provider: 'DeepSeek / Kie.ai', providerKey: 'KIE_API_KEY', endpoint: PROVIDER_ENDPOINTS.kie, modelParam: 'deepseek-chat', defaultModelParam: 'deepseek-chat', context: 128000 },

  // OpenRouter (200+ models)
  { id: 'openrouter', name: 'OpenRouter (200+ models)', provider: 'OpenRouter', providerKey: 'OPENROUTER_API_KEY', endpoint: PROVIDER_ENDPOINTS.openrouter, modelParam: 'openrouter/auto', defaultModelParam: 'openrouter/auto', vision: true, context: 128000 },
  { id: 'qwen-coder', name: 'Qwen 2.5 Coder 32B', provider: 'Alibaba', providerKey: 'OPENROUTER_API_KEY', endpoint: PROVIDER_ENDPOINTS.qwen, modelParam: 'qwen/qwen-2.5-coder-32b-instruct', defaultModelParam: 'qwen/qwen-2.5-coder-32b-instruct', context: 128000 },

  // Groq (free tier)
  { id: 'groq-llama', name: 'Llama 3.3 70B (Groq)', provider: 'Meta / Groq', providerKey: 'GROQ_API_KEY', endpoint: PROVIDER_ENDPOINTS.groq, modelParam: 'llama-3.3-70b-versatile', defaultModelParam: 'llama-3.3-70b-versatile', free: true, context: 128000 },
  { id: 'groq-fast', name: 'Llama 3.1 8B (Groq Free)', provider: 'Meta / Groq', providerKey: 'GROQ_API_KEY', endpoint: PROVIDER_ENDPOINTS.groq, modelParam: 'llama-3.1-8b-instant', defaultModelParam: 'llama-3.1-8b-instant', free: true, context: 131072 },

  // NVIDIA NIM
  { id: 'nvidia-llama', name: 'Llama 3.3 70B (NVIDIA NIM)', provider: 'NVIDIA', providerKey: 'NVIDIA_API_KEY', endpoint: PROVIDER_ENDPOINTS.nvidia, modelParam: 'meta/llama-3.3-70b-instruct', defaultModelParam: 'meta/llama-3.3-70b-instruct', context: 128000 },

  // Mistral
  { id: 'mistral-large', name: 'Mistral Large 2', provider: 'Mistral AI', providerKey: 'MISTRAL_API_KEY', endpoint: PROVIDER_ENDPOINTS.mistral, modelParam: 'mistral-large-latest', defaultModelParam: 'mistral-large-latest', context: 128000 },

  // BazaarLink universal gateway
  { id: 'bazaarlink', name: 'BazaarLink Universal', provider: 'BazaarLink.ai', providerKey: 'BAZAARLINK_API_KEY', endpoint: PROVIDER_ENDPOINTS.bazaarlink, modelParam: 'gpt-4o', defaultModelParam: 'gpt-4o', vision: true, context: 1000000 },

  // Hugging Face (free inference)
  { id: 'hf-codellama', name: 'CodeLlama 34B (HF)', provider: 'Hugging Face', providerKey: 'HF_API_KEY', endpoint: PROVIDER_ENDPOINTS.huggingface, modelParam: 'codellama/CodeLlama-34b-Instruct-hf', defaultModelParam: 'codellama/CodeLlama-34b-Instruct-hf', free: true, context: 16000 },
  { id: 'hf-llama', name: 'Llama 3.2 3B (HF Free)', provider: 'Hugging Face / Meta', providerKey: 'HF_API_KEY', endpoint: PROVIDER_ENDPOINTS.huggingface, modelParam: 'meta-llama/Llama-3.2-3B-Instruct', defaultModelParam: 'meta-llama/Llama-3.2-3B-Instruct', free: true, context: 8000 },
];

export function getModel(id: string): ProviderModel | undefined {
  if (id === 'auto') return undefined;
  return MODEL_REGISTRY.find((m) => m.id === id);
}

export function listModels(): Omit<ProviderModel, 'providerKey'>[] {
  return MODEL_REGISTRY.map(({ providerKey: _k, ...rest }) => rest);
}

export const env = {
  GEMINI_API_KEY: process.env.GEMINI_API_KEY ?? '',
  OPENAI_API_KEY: process.env.OPENAI_API_KEY ?? '',
  ANTHROPIC_API_KEY: process.env.ANTHROPIC_API_KEY ?? '',
  GROQ_API_KEY: process.env.GROQ_API_KEY ?? '',
  OPENROUTER_API_KEY: process.env.OPENROUTER_API_KEY ?? '',
  KIE_API_KEY: process.env.KIE_API_KEY ?? '',
  NVIDIA_API_KEY: process.env.NVIDIA_API_KEY ?? '',
  MISTRAL_API_KEY: process.env.MISTRAL_API_KEY ?? '',
  BAZAARLINK_API_KEY: process.env.BAZAARLINK_API_KEY ?? '',
  HF_API_KEY: process.env.HF_API_KEY ?? '',
};

export const SYSTEM_PROMPT = `You are VirgoYT Cloud AI — an autonomous cloud software engineer and multi-agent co-developer.
You run inside a cloud sandbox where you can write code, run commands, create files, and browse.
Be concise, practical, and produce production-grade code. Use markdown code blocks.
If asked to build something, provide the full files and clear steps.`;

export type GatewayResult = { content: string; model: string; provider: string; keyConfigured: boolean };

export async function route(req: GatewayRequest): Promise<GatewayResult> {
  const model = getModel(req.model);
  const messages = buildMessages(req);

  if (!model) {
    // Auto-routing: pick the best available configured model by intent.
    return autoRoute(req, messages);
  }

  const key = env[model.providerKey];
  if (!key) {
    return { content: keyMissingMessage(model), model: model.id, provider: model.provider, keyConfigured: false };
  }

  return callProvider(model, messages, req, key);
}

async function autoRoute(req: GatewayRequest, messages: any): Promise<GatewayResult> {
  const p = req.prompt.toLowerCase();
  const freeOnly = p.includes('free') || p.includes('cheap');

  // Try Gemini as the primary default if configured
  const candidates: string[] = [];
  if (env.GEMINI_API_KEY) candidates.push('gemini-flash', 'gemini-pro');
  if (env.GROQ_API_KEY) candidates.push('groq-fast');
  if (env.OPENAI_API_KEY) candidates.push('gpt-4o-mini', 'gpt-4o');

  const pick = (ids: string[]): string | undefined => {
    const configured = ids.filter((id) => env[getModel(id)!.providerKey]);
    return configured[0];
  };

  // Intent-based routing
  let target: string | undefined;
  if (p.includes('math') || p.includes('proof') || p.includes('algorithm') || p.includes('complex')) {
    target = pick(['deepseek-r1', 'gemini-pro', 'gpt-4o', 'claude-sonnet']);
  } else if (p.includes('3d') || p.includes('three') || p.includes('game') || p.includes('unreal')) {
    target = pick(['claude-sonnet', 'gemini-pro', 'gpt-4o']);
  } else if (p.includes('speed') || p.includes('fast') || p.includes('summarize') || freeOnly) {
    target = pick(env.GROQ_API_KEY ? ['groq-fast', 'gemini-flash'] : ['gemini-flash', 'groq-fast']);
  } else if (p.includes('code') || p.includes('file') || p.includes('function') || p.includes('build')) {
    target = pick(['qwen-coder', 'gpt-4o-mini', 'gemini-flash']);
  } else {
    target = pick(['gemini-flash', 'gpt-4o-mini', 'groq-fast']);
  }

  if (target) {
    const m = getModel(target)!;
    return callProvider(m, messages, req, env[m.providerKey]);
  }

  return { content: fallbackReply(req.prompt), model: 'fallback', provider: 'local', keyConfigured: false };
}

function buildMessages(req: GatewayRequest) {
  const system = req.system ?? SYSTEM_PROMPT;
  const history = (req.history ?? []).map((h) => ({ role: h.role, content: h.content }));
  return [...history, { role: 'user', content: req.prompt }];
}

async function callProvider(model: ProviderModel, messages: any, req: GatewayRequest, apiKey: string): Promise<GatewayResult> {
  try {
    if (model.providerKey === 'GEMINI_API_KEY') {
      return await callGemini(model, messages, req, apiKey);
    }
    if (model.providerKey === 'HF_API_KEY') {
      return await callHuggingFace(model, messages, req, apiKey);
    }
    if (model.providerKey === 'ANTHROPIC_API_KEY') {
      return await callAnthropic(model, messages, req, apiKey);
    }
    return await callOpenAICompatible(model, messages, req, apiKey);
  } catch (e) {
    return { content: `Provider error (${model.provider}): ${(e as Error).message}`, model: model.id, provider: model.provider, keyConfigured: true };
  }
}

// OpenAI-compatible /v1/chat/completions
async function callOpenAICompatible(model: ProviderModel, messages: any, req: GatewayRequest, apiKey: string): Promise<GatewayResult> {
  const url = `${model.endpoint}/chat/completions`;
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${apiKey}`,
  };
  if (model.providerKey === 'OPENROUTER_API_KEY') {
    headers['HTTP-Referer'] = 'https://github.com/darkvirgoyt-beep/VirgoYT-AI';
    headers['X-Title'] = 'VirgoYT Cloud AI';
  }

  const body: any = {
    model: model.modelParam,
    messages,
    temperature: req.temperature ?? 0.7,
    max_tokens: req.maxTokens ?? (model.context && model.context < 50000 ? 2048 : 4096),
  };

  const res = await fetch(url, { method: 'POST', headers, body: JSON.stringify(body) });
  if (!res.ok) {
    const err = await res.text();
    throw new Error(`${res.status} ${err.slice(0, 300)}`);
  }
  const data = (await res.json()) as { choices?: { message?: { content?: string } }[] };
  const content = data?.choices?.[0]?.message?.content ?? '';
  return { content, model: model.id, provider: model.provider, keyConfigured: true };
}

// Gemini generateContent
async function callGemini(model: ProviderModel, messages: any, req: GatewayRequest, apiKey: string): Promise<GatewayResult> {
  const url = `${model.endpoint}/${model.defaultModelParam}:generateContent?key=${apiKey}`;
  const contents = messages
    .filter((m: any) => m.role !== 'system')
    .map((m: any) => ({ role: m.role === 'assistant' ? 'model' : 'user', parts: [{ text: m.content }] }));
  const systemText = messages.find((m: any) => m.role === 'system')?.content ?? SYSTEM_PROMPT;

  const body = {
    contents,
    systemInstruction: { parts: [{ text: systemText }] },
    generationConfig: { temperature: req.temperature ?? 0.7, maxOutputTokens: 4096 },
  };

  const res = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
  if (!res.ok) {
    const err = await res.text();
    throw new Error(`${res.status} ${err.slice(0, 300)}`);
  }
  const data = (await res.json()) as { candidates?: { content?: { parts?: { text?: string }[] } }[] };
  const text = data?.candidates?.[0]?.content?.parts?.map((p) => p.text).join('') ?? '';
  return { content: text, model: model.id, provider: model.provider, keyConfigured: true };
}

// Anthropic Messages API
async function callAnthropic(model: ProviderModel, messages: any, req: GatewayRequest, apiKey: string): Promise<GatewayResult> {
  const url = `${model.endpoint}/messages`;
  const system = messages.find((m: any) => m.role === 'system')?.content ?? SYSTEM_PROMPT;
  const nonSystem = messages.filter((m: any) => m.role !== 'system').map((m: any) => ({
    role: m.role === 'assistant' ? 'assistant' : 'user',
    content: m.content,
  }));

  const body = {
    model: model.modelParam,
    system,
    messages: nonSystem,
    max_tokens: 2048,
    temperature: req.temperature ?? 0.7,
  };

  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'x-api-key': apiKey, 'anthropic-version': '2023-06-01' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.text();
    throw new Error(`${res.status} ${err.slice(0, 300)}`);
  }
  const data = (await res.json()) as { content?: { text?: string }[] };
  const content = data?.content?.map((c) => c.text).join('') ?? '';
  return { content, model: model.id, provider: model.provider, keyConfigured: true };
}

// Hugging Face inference
async function callHuggingFace(model: ProviderModel, messages: any, req: GatewayRequest, apiKey: string): Promise<GatewayResult> {
  const text = messages.map((m: any) => `${m.role === 'user' ? 'User' : 'Assistant'}: ${m.content}`).join('\n\n') + `\n\nAssistant:`;
  const url = `https://api-inference.huggingface.co/models/${model.modelParam}`;
  const res = await fetch(url, {
    method: 'POST',
    headers: { Authorization: `Bearer ${apiKey}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ inputs: text, parameters: { max_new_tokens: 512 } }),
  });
  if (!res.ok) {
    const err = await res.text();
    throw new Error(`${res.status} ${err.slice(0, 300)}`);
  }
  const data = (await res.json()) as { generated_text?: string }[] | { error?: string };
  const content = Array.isArray(data) ? data[0]?.generated_text ?? '' : (data as any).error ?? '';
  return { content, model: model.id, provider: model.provider, keyConfigured: true };
}

function keyMissingMessage(model: ProviderModel): string {
  const keyName = model.providerKey;
  return `🔑 **${model.name}** is not configured yet.\n\nSet the \`${keyName}\` environment variable on your server/Vercel to enable this provider. For free options, add a \`GROQ_API_KEY\` or \`HF_API_KEY\` (both have free tiers).`;
}

function fallbackReply(prompt: string): string {
  const p = prompt.toLowerCase();
  if (p.includes('hello') || p.includes('hi')) {
    return `👋 Hello! I'm **VirgoYT Cloud AI**.\n\nNo AI API keys are configured on this server yet. Add any of these to enable live AI:\n\n- \`GEMINI_API_KEY\` (Google — free tier)\n- \`OPENAI_API_KEY\` (OpenAI / ChatGPT)\n- \`ANTHROPIC_API_KEY\` (Claude)\n- \`GROQ_API_KEY\` (free Llama!)\n- \`HF_API_KEY\` (Hugging Face — free)\n- \`OPENROUTER_API_KEY\` (200+ models)\n\nOr ask me to help with something and I'll respond in local mode.`;
  }
  if (p.includes('terminal') || p.includes('command')) {
    return "I'll help you run that in the terminal panel. Tell me the task and I'll prepare the command.";
  }
  return `I received: "${prompt}"\n\n*(No AI provider configured — add an API key on the server to enable live responses.)*`;
}
