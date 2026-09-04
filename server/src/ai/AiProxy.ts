import { route, listModels, type GatewayRequest } from './AiGateway.js';

export type AiRequest = GatewayRequest;

/**
 * Primary path: forward to the user's own Google Apps Script web app if
 * GOOGLE_SCRIPT_URL is set (deployed, no-fee reasoning). Otherwise route
 * through the multi-provider gateway.
 */
export async function proxyAi(req: AiRequest): Promise<{ content: string; model: string; provider: string; keyConfigured: boolean }> {
  const scriptUrl = process.env.GOOGLE_SCRIPT_URL?.trim();
  if (scriptUrl) {
    return callGoogleScript(scriptUrl, req);
  }
  return route(req);
}

async function callGoogleScript(
  url: string,
  req: AiRequest
): Promise<{ content: string; model: string; provider: string; keyConfigured: boolean }> {
  const controller = new AbortController();
  const t = setTimeout(() => controller.abort(), 25000);
  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        prompt: req.prompt,
        model: req.model ?? 'auto',
        history: req.history ?? [],
        system: req.system ?? undefined,
      }),
      signal: controller.signal,
    });
    const text = await res.text();
    const data = safeJson(text);
    const content = extractText(data ?? text);
    if (!content) {
      throw new Error(`Google Script returned no text (HTTP ${res.status}). Body: ${text.slice(0, 200)}`);
    }
    return { content, model: req.model ?? 'google-script', provider: 'Google Apps Script', keyConfigured: true };
  } catch (e: any) {
    if (aborted(e)) throw new Error('Google Script timed out (25s). Is your deployment set to "Anyone" with "Execute as Me"?');
    throw e;
  } finally {
    clearTimeout(t);
  }
}

// ---- response parsing helpers (covers common connectors) ----

function safeJson(text: string): any {
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

function extractText(data: any): string {
  if (typeof data === 'string') return data.trim();
  if (!data) return '';
  // { content: "..." } or { reply: "..." } or { answer: "..." }
  for (const k of ['content', 'reply', 'answer', 'text', 'response', 'output']) {
    if (typeof data[k] === 'string' && data[k].trim()) return data[k].trim();
  }
  // { data: { content: "..." } } nested
  const inner = data.data ?? data.result ?? data.body;
  if (inner && typeof inner === 'object') return extractText(inner);
  // Array of candidates / choices (Gemini or OpenAI style)
  if (Array.isArray(data.candidates)) {
    const c = data.candidates[0];
    const t = c?.content?.parts?.map((p: any) => p.text).join('') || c?.text || c?.message?.content;
    if (typeof t === 'string' && t.trim()) return t.trim();
  }
  if (Array.isArray(data.choices)) {
    const c = data.choices[0]?.message?.content;
    if (typeof c === 'string' && c.trim()) return c.trim();
  }
  return '';
}

function aborted(e: any): boolean {
  return e?.name === 'AbortError' || (typeof e?.message === 'string' && e.message.toLowerCase().includes('abort'));
}

export function getAvailableModels() {
  return listModels();
}