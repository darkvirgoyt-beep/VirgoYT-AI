// Long-term memory store.
// JSON-backed, per-session key. Remembers preferences, projects, decisions,
// prior work and learned skills so the agent improves over time.
// Mirrors the style of db/Database.ts (no native deps).

import { join, dirname } from 'path';
import { mkdirSync, existsSync, readFileSync, writeFileSync } from 'fs';

export type MemoryEntry = {
  key: string; // store scope: sessionId or ROOT_KEY
  kind: 'preference' | 'project' | 'decision' | 'fact' | 'skill' | 'lesson';
  name: string; // semantic key, e.g. "theme", "project:website"
  value: string;
  ts: number;
};

type MemoryShape = { entries: MemoryEntry[] };

export const ROOT_KEY = '__global__';

let mem: MemoryShape = { entries: [] };
let memPath: string = '';

export function initMemory(filePath?: string): void {
  memPath = filePath ?? process.env.MEMORY_PATH ?? join(process.cwd(), 'data', 'memory.json');
  mkdirSync(dirname(memPath), { recursive: true });
  if (existsSync(memPath)) {
    try {
      mem = JSON.parse(readFileSync(memPath, 'utf8'));
      if (!Array.isArray(mem.entries)) mem = { entries: [] };
    } catch {
      mem = { entries: [] };
    }
  } else {
    mem = { entries: [] };
    persistMem();
  }
}

function persistMem() {
  if (!memPath) return;
  writeFileSync(memPath, JSON.stringify(mem, null, 2), 'utf8');
}

function storeKey(sessionId: string): string {
  return sessionId && sessionId !== 'undefined' ? sessionId : ROOT_KEY;
}

export function remember(sessionId: string, kind: MemoryEntry['kind'], name: string, value: string): void {
  const scope = storeKey(sessionId);
  mem.entries = mem.entries.filter((e) => !(e.key === scope && e.kind === kind && e.name === name));
  mem.entries.push({ key: scope, kind, name, value, ts: Date.now() });
  if (mem.entries.length > 5000) mem.entries = mem.entries.slice(-5000);
  persistMem();
}

export function recall(sessionId: string, kinds?: MemoryEntry['kind'][]): MemoryEntry[] {
  const scope = storeKey(sessionId);
  const scoped = mem.entries.filter((e) => e.key === scope || scope === ROOT_KEY);
  const sorted = scoped.sort((a, b) => b.ts - a.ts);
  return kinds ? sorted.filter((e) => kinds.includes(e.kind)) : sorted;
}

export function rememberShared(kind: MemoryEntry['kind'], name: string, value: string): void {
  remember(ROOT_KEY, kind, name, value);
}

export function clearMemory(sessionId: string): void {
  const scope = storeKey(sessionId);
  if (scope === ROOT_KEY) mem.entries = [];
  else mem.entries = mem.entries.filter((e) => e.key !== scope);
  persistMem();
}

export function toPrompt(sessionId: string, limit = 12): string {
  const items = recall(sessionId).slice(0, limit);
  if (!items.length) return 'No long-term memory yet.';
  return items.map((e) => `[${e.kind}] ${e.name}: ${e.value}`).join('\n');
}