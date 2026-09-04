export type ProviderId = 'github' | 'vercel' | 'supabase' | 'render';

const providers: Array<{ id: ProviderId; name: string; envKey: string }> = [
  { id: 'github', name: 'GitHub', envKey: 'GITHUB_TOKEN' },
  { id: 'vercel', name: 'Vercel', envKey: 'VERCEL_TOKEN' },
  { id: 'supabase', name: 'Supabase', envKey: 'SUPABASE_ACCESS_TOKEN' },
  { id: 'render', name: 'Render', envKey: 'RENDER_API_KEY' },
];

export function listConnectors(authorized: ProviderId[] = []): Array<{ id: ProviderId; name: string; configured: boolean; authorized: boolean }> {
  return providers.map((provider) => ({
    id: provider.id,
    name: provider.name,
    configured: Boolean(process.env[provider.envKey]),
    authorized: authorized.includes(provider.id),
  }));
}

export function isConfigured(provider: ProviderId): boolean {
  const entry = providers.find((item) => item.id === provider);
  return Boolean(entry && process.env[entry.envKey]);
}
