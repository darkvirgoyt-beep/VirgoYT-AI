import { randomUUID } from 'node:crypto';
import { readFile, writeFile, mkdir } from 'node:fs/promises';
import path from 'node:path';
import type { ProviderId } from './DeployConnectors.js';

type TokenRecord = { token: string; createdAt: number };
const memory = new Map<ProviderId, TokenRecord>();
const tokenPath = process.env.OAUTH_TOKEN_PATH ?? path.join(process.env.WORKSPACE_ROOT ?? './workspaces', '.oauth-tokens.json');

async function load(): Promise<void> {
  try {
    const data = JSON.parse(await readFile(tokenPath, 'utf8')) as Record<string, TokenRecord>;
    for (const [id, record] of Object.entries(data)) memory.set(id as ProviderId, record);
  } catch {
    // First run or an unavailable optional token store.
  }
}

async function persist(): Promise<void> {
  await mkdir(path.dirname(tokenPath), { recursive: true });
  await writeFile(tokenPath, JSON.stringify(Object.fromEntries(memory), null, 2), { mode: 0o600 });
}

void load();

export function authorizedIds(): ProviderId[] { return [...memory.keys()]; }
export function hasStoredToken(provider: ProviderId): boolean { return memory.has(provider); }
export function tokenMeta(provider: ProviderId) {
  const record = memory.get(provider);
  return record ? { stored: true, createdAt: record.createdAt } : { stored: false };
}
export async function storeToken(provider: ProviderId, token: string): Promise<void> {
  memory.set(provider, { token, createdAt: Date.now() });
  await persist();
}
export async function clearTokens(): Promise<void> {
  memory.clear();
  await persist();
}

export async function exchangeCode(provider: ProviderId, code: string): Promise<string> {
  const envPrefix = provider.toUpperCase();
  const clientId = process.env[`${envPrefix}_CLIENT_ID`];
  const clientSecret = process.env[`${envPrefix}_CLIENT_SECRET`];
  if (!clientId || !clientSecret) {
    throw new Error(`${provider} OAuth credentials are not configured on the server`);
  }
  const tokenUrl: Record<ProviderId, string> = {
    github: 'https://github.com/login/oauth/access_token',
    vercel: 'https://api.vercel.com/v2/oauth/access_token',
    supabase: 'https://api.supabase.com/v1/oauth/token',
    render: 'https://api.render.com/oauth/token',
  };
  const response = await fetch(tokenUrl[provider], {
    method: 'POST',
    headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
    body: JSON.stringify({ client_id: clientId, client_secret: clientSecret, code }),
  });
  const body = await response.json() as { access_token?: string; token?: string; error_description?: string };
  if (!response.ok) throw new Error(body.error_description ?? `OAuth exchange failed (${response.status})`);
  const token = body.access_token ?? body.token;
  if (!token) throw new Error('OAuth provider returned no access token');
  return token;
}

export function createState(): string { return randomUUID(); }
