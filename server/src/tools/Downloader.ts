// Download files (APK, EXE, videos, images, zips, etc.) into the sandbox.

import { createWriteStream } from 'fs';
import { mkdirSync } from 'fs';
import { join, dirname, basename } from 'path';
import { pipeline } from 'stream/promises';
import { setTimeout as delay } from 'timers/promises';

export type DownloadResult = { path: string; size: number };

export async function downloadFile(url: string, rootDir: string, destDir = 'downloads'): Promise<DownloadResult> {
  const targetDir = join(rootDir, destDir);
  mkdirSync(targetDir, { recursive: true });

  const filename = deriveFilename(url);
  const filePath = join(targetDir, filename);

  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 120000);

  const res = await fetch(url, { redirect: 'follow', signal: controller.signal });
  if (!res.ok || !res.body) {
    throw new Error(`Download failed: HTTP ${res.status}`);
  }

  await pipeline(res.body, createWriteStream(filePath));
  clearTimeout(timeout);

  const { stat } = await import('fs/promises');
  const size = (await stat(filePath)).size;

  return { path: filePath, size };
}

function deriveFilename(url: string): string {
  const clean = url.split('?')[0].split('#')[0];
  const name = basename(clean);
  const safe = name.replace(/[^a-zA-Z0-9._-]/g, '_');
  if (safe.includes('.')) return safe;
  // no extension — guess
  const ext = guessExtension(url);
  return safe ? `${safe}${ext}` : `download${Date.now()}${ext}`;
}

function guessExtension(url: string): string {
  if (url.includes('.apk')) return '.apk';
  if (url.includes('.exe')) return '.exe';
  if (url.includes('.mp4')) return '.mp4';
  if (url.includes('.png')) return '.png';
  if (url.includes('.jpg') || url.includes('.jpeg')) return '.jpg';
  if (url.includes('.gif')) return '.gif';
  if (url.includes('.zip')) return '.zip';
  if (url.includes('.pdf')) return '.pdf';
  return '.bin';
}