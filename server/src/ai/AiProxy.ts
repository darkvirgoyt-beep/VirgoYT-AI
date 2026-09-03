const GEMINI_API_KEY = process.env.GEMINI_API_KEY ?? '';

export type AiRequest = {
  model: string;
  prompt: string;
  system?: string;
  history?: { role: string; content: string }[];
};

const GEMINI_MODELS: Record<string, string> = {
  'gemini-flash': 'gemini-2.0-flash',
  'gemini-pro': 'gemini-1.5-pro',
};

export async function proxyAi(req: AiRequest): Promise<{ content: string; model: string }> {
  const modelId = req.model ?? 'auto';

  // Gemini native
  if (modelId === 'gemini-flash' || modelId === 'gemini-pro') {
    if (!GEMINI_API_KEY) {
      return { content: fallbackReply(req.prompt), model: 'local' };
    }
    try {
      return await queryGemini(modelId, req);
    } catch {
      return { content: fallbackReply(req.prompt), model: 'local' };
    }
  }

  // Default: try Gemini flash, else local fallback
  if (GEMINI_API_KEY) {
    try {
      return await queryGemini('gemini-flash', req);
    } catch {
      return { content: fallbackReply(req.prompt), model: 'local' };
    }
  }

  return { content: fallbackReply(req.prompt), model: 'local' };
}

async function queryGemini(modelKey: string, req: AiRequest) {
  const model = GEMINI_MODELS[modelKey] ?? 'gemini-2.0-flash';
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${GEMINI_API_KEY}`;

  const contents = [
    ...(req.history ?? []).map((h) => ({
      role: h.role === 'assistant' ? 'model' : 'user',
      parts: [{ text: h.content }],
    })),
    { role: 'user', parts: [{ text: req.prompt }] },
  ];

  const body = {
    contents,
    systemInstruction: req.system
      ? { parts: [{ text: req.system }] }
      : { parts: [{ text: SYSTEM_PROMPT }] },
    generationConfig: { temperature: 0.7, maxOutputTokens: 2048 },
  };

  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const errText = await res.text();
    throw new Error(`Gemini error ${res.status}: ${errText}`);
  }

  const data = (await res.json()) as {
    candidates?: { content?: { parts?: { text?: string }[] } }[];
  };
  const text = data?.candidates?.[0]?.content?.parts?.map((p: any) => p.text).join('') ?? '';
  return { content: text, model: modelKey };
}

const SYSTEM_PROMPT = `You are VirgoYT Cloud AI, an autonomous cloud software engineer running inside a cloud sandbox.
You can help users write code, run commands, create files, and build applications.
Be concise and practical. Provide code in markdown blocks.`;

function fallbackReply(prompt: string): string {
  const p = prompt.toLowerCase();
  if (p.includes('hello') || p.includes('hi')) {
    return `Hello! I'm VirgoYT Cloud AI. Connect a GEMINI_API_KEY to enable my full AI brain. Ask me to build something.`;
  }
  if (p.includes('terminal') || p.includes('command')) {
    return "I'll help you run that in the terminal panel. Say the word and I'll prepare the command.";
  }
  return `I received: "${prompt}"\n\n(Set GEMINI_API_KEY on the server to enable live AI responses. This is the offline fallback engine.)`;
}
