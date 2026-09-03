import { promises as fs } from 'fs';
import { join, resolve, dirname, basename } from 'path';

export type FileNode = {
  name: string;
  path: string;
  type: 'file' | 'directory';
  children?: FileNode[];
  size?: number;
};

export async function listTree(rootDir: string, base = ''): Promise<FileNode[]> {
  const full = base ? join(rootDir, base) : rootDir;
  const entries = await fs.readdir(full, { withFileTypes: true });
  const nodes: FileNode[] = [];
  for (const entry of entries) {
    if (entry.name === 'node_modules' || entry.name === '.git' || entry.name.startsWith('.')) {
      continue;
    }
    const rel = base ? `${base}/${entry.name}` : entry.name;
    const abs = join(rootDir, rel);
    if (entry.isDirectory()) {
      nodes.push({
        name: entry.name,
        path: `/${rel}`,
        type: 'directory',
        children: await listTree(rootDir, rel),
      });
    } else {
      let size = 0;
      try {
        size = (await fs.stat(abs)).size;
      } catch {}
      nodes.push({ name: entry.name, path: `/${rel}`, type: 'file', size });
    }
  }
  return nodes;
}

export async function readFile(rootDir: string, relPath: string): Promise<{ content: string }> {
  const safe = safeJoin(rootDir, relPath);
  const content = await fs.readFile(safe, 'utf8');
  return { content };
}

export async function writeFile(
  rootDir: string,
  relPath: string,
  content: string
): Promise<{ path: string; ok: true }> {
  const safe = safeJoin(rootDir, relPath);
  await fs.mkdir(dirname(safe), { recursive: true });
  await fs.writeFile(safe, content, 'utf8');
  return { path: `/${relPath}`, ok: true };
}

export async function createDirectory(rootDir: string, relPath: string): Promise<{ path: string; ok: true }> {
  const safe = safeJoin(rootDir, relPath);
  await fs.mkdir(safe, { recursive: true });
  return { path: `/${relPath}`, ok: true };
}

export async function deleteEntry(rootDir: string, relPath: string): Promise<{ ok: true }> {
  const safe = safeJoin(rootDir, relPath);
  const stat = await fs.stat(safe);
  if (stat.isDirectory()) {
    await fs.rm(safe, { recursive: true, force: true });
  } else {
    await fs.unlink(safe);
  }
  return { ok: true };
}

export async function renameEntry(rootDir: string, from: string, to: string): Promise<{ ok: true }> {
  const safeFrom = safeJoin(rootDir, from);
  const safeTo = safeJoin(rootDir, to);
  await fs.mkdir(dirname(safeTo), { recursive: true });
  await fs.rename(safeFrom, safeTo);
  return { ok: true };
}

function safeJoin(rootDir: string, relPath: string): string {
  const clean = relPath.replace(/^\/+/, '');
  const resolved = resolve(rootDir, clean);
  if (!resolved.startsWith(resolve(rootDir))) {
    throw new Error('Path traversal detected');
  }
  return resolved;
}
