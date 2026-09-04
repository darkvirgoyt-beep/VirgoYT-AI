import { readdir, stat } from 'node:fs/promises';
import path from 'node:path';

export type ExportTarget = 'web' | 'exe' | 'apk' | 'mac' | 'terminal';

async function collectFiles(root: string, current = root, output: string[] = []): Promise<string[]> {
  const entries = await readdir(current, { withFileTypes: true });
  for (const entry of entries) {
    if (entry.name === 'node_modules' || entry.name === '.git' || entry.name === 'dist') continue;
    const full = path.join(current, entry.name);
    if (entry.isDirectory()) await collectFiles(root, full, output);
    else if ((await stat(full)).isFile()) output.push(path.relative(root, full));
  }
  return output;
}

export async function exportProject(root: string, target: ExportTarget, appName: string) {
  const files = await collectFiles(root);
  return {
    ok: true,
    target,
    appName,
    root,
    files,
    message: `Prepared ${files.length} project files for ${target} export.`,
  };
}
