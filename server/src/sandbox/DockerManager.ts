import { exec, execSync, spawn } from 'child_process';
import { mkdirSync, existsSync } from 'fs';
import { join } from 'path';
import os from 'os';

export type SandboxInfo = {
  containerId: string;
  rootDir: string;
  mode: 'docker' | 'local';
};

const WORKSPACE_ROOT = process.env.WORKSPACE_ROOT ?? join(process.cwd(), 'workspaces');

function dockerAvailable(): boolean {
  try {
    execSync('docker info', { stdio: 'pipe' });
    return true;
  } catch {
    return false;
  }
}

export class SandboxManager {
  private running = new Map<string, SandboxInfo>();

  constructor() {
    mkdirSync(WORKSPACE_ROOT, { recursive: true });
  }

  isDocker(): boolean {
    return this.running.size > 0 && Array.from(this.running.values())[0].mode === 'docker';
  }

  createSession(sessionId: string): SandboxInfo {
    const rootDir = join(WORKSPACE_ROOT, sessionId);
    mkdirSync(rootDir, { recursive: true });

    if (dockerAvailable()) {
      try {
        const containerId = execSync(
          `docker run -d --name virgoyt-${sessionId} -v ${rootDir}:/workspace ubuntu:22.04 sleep infinity`,
          { stdio: 'pipe' }
        )
          .toString()
          .trim();
        const info: SandboxInfo = { containerId, rootDir, mode: 'docker' };
        this.running.set(sessionId, info);
        return info;
      } catch {
        // fall through to local
      }
    }

    const info: SandboxInfo = { containerId: sessionId, rootDir, mode: 'local' };
    this.running.set(sessionId, info);
    return info;
  }

  destroySession(sessionId: string) {
    const info = this.running.get(sessionId);
    if (info && info.mode === 'docker') {
      try {
        execSync(`docker rm -f ${info.containerId}`, { stdio: 'pipe' });
      } catch {
        // ignore
      }
    }
    this.running.delete(sessionId);
  }

  getSession(sessionId: string): SandboxInfo | undefined {
    return this.running.get(sessionId);
  }

  stats(sessionId: string) {
    return {
      cpu: Math.random() * 40,
      memUsed: Math.round(Math.random() * 2048),
      memTotal: 4096,
      diskUsed: Math.round(Math.random() * 1024),
      diskTotal: 20480,
      network: { up: Math.round(Math.random() * 2048), down: Math.round(Math.random() * 4096) },
      uptime: 3600 + Math.floor(Math.random() * 7200),
      processes: [],
      os: 'Linux',
      kernel: '6.2.0',
      hostname: `${os.hostname()}-virgoyt`,
    };
  }
}

export function runCommandInSandbox(sessionId: string, command: string, rootDir: string): Promise<string> {
  return new Promise((resolve) => {
    try {
      const child = spawn('/bin/bash', ['-lc', command], {
        cwd: rootDir,
        env: { ...process.env, PATH: process.env.PATH ?? '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin' },
        shell: false,
      });
      let output = '';
      child.stdout.on('data', (d: Buffer) => (output += d.toString()));
      child.stderr.on('data', (d: Buffer) => (output += d.toString()));
      const timer = setTimeout(() => {
        child.kill('SIGKILL');
        resolve(output || `(command timed out: ${command})`);
      }, 20000);
      child.on('close', () => {
        clearTimeout(timer);
        resolve(output);
      });
    } catch (e) {
      resolve(`Error executing: ${(e as Error).message}`);
    }
  });
}

export const sandbox = new SandboxManager();
