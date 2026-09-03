// Terminal PTY manager.
// Uses node-pty for real shell when available, with a graceful fallback otherwise.

import { join } from 'path';

type PtyLike = {
  write: (data: string) => void;
  resize: (cols: number, rows: number) => void;
  kill: () => void;
};

type PtySession = {
  pty: PtyLike;
  id: string;
  real: boolean;
};

const sessions = new Map<string, PtySession>();

let ptyHub: { onData?: (sessionId: string, data: string) => void; onExit?: (sessionId: string) => void } = {};

export function setPtyCallbacks(callbacks: { onData: (id: string, d: string) => void; onExit: (id: string) => void }) {
  ptyHub = callbacks;
}

let nodePty: any = null;
try {
  nodePty = require('node-pty');
} catch {
  nodePty = null;
}

function isRealPty(): boolean {
  return !!nodePty;
}

export function createPty(sessionId: string, cwd: string): PtySession {
  destroyPty(sessionId);

  if (nodePty) {
    const shell = process.env.SHELL ?? '/bin/bash';
    const pty = nodePty.spawn(shell, [], {
      name: 'xterm-256color',
      cols: 80,
      rows: 24,
      cwd,
      env: { ...process.env, TERM: 'xterm-256color', COLORTERM: 'truecolor' },
    });

    pty.onData((data: string) => ptyHub.onData?.(sessionId, data));
    pty.onExit(() => {
      ptyHub.onExit?.(sessionId);
      sessions.delete(sessionId);
    });

    const session: PtySession = { pty, id: sessionId, real: true };
    sessions.set(sessionId, session);
    return session;
  }

  // Fallback demo PTY (no native shell available)
  const demo = createDemoPty(sessionId);
  const session: PtySession = { pty: demo, id: sessionId, real: false };
  sessions.set(sessionId, session);
  return session;
}

function createDemoPty(sessionId: string): PtyLike {
  let buffer = '';
  return {
    write: (data: string) => {
      buffer += data;
      if (buffer.includes('\r')) {
        const lines = buffer.split('\r');
        const cmd = lines[lines.length - 1].trim();
        buffer = '';
        ptyHub.onData?.(sessionId, `\r\n`);
        handleDemoCommand(sessionId, cmd, (out) => ptyHub.onData?.(sessionId, out));
      }
    },
    resize: () => {},
    kill: () => {
      sessions.delete(sessionId);
    },
  };
}

function handleDemoCommand(sessionId: string, cmd: string, emit: (out: string) => void) {
  const parts = cmd.split(/[\s]+/).filter(Boolean);
  const name = parts[0] ?? '';
  switch (name) {
    case 'help':
      emit(
        '\r\n\x1b[36mVirgoYT Cloud Terminal (demo mode)\x1b[0m\r\nAvailable: help, ls, pwd, echo, node, python, git, clear\r\nStart the backend with Docker for a full Linux shell.\r\n'
      );
      break;
    case 'pwd':
      emit(`\r\n${join(process.cwd(), 'workspaces', sessionId)}\r\n`);
      break;
    case 'ls':
      emit('\r\nREADME.md   package.json   src\r\n');
      break;
    case 'echo':
      emit(`\r\n${parts.slice(1).join(' ')}\r\n`);
      break;
    case 'clear':
      break;
    case 'node':
    case 'python':
    case 'python3':
    case 'git':
    case 'npm':
      emit(`\r\n\x1b[33m(demo)\x1b[0m Connect the backend with Docker to run ${name} commands in a real sandbox.\r\n`);
      break;
    default:
      if (cmd.trim()) {
        emit(`\r\n\x1b[33m(demo)\x1b[0m ${name}: command not simulated. Launch the real backend for a full shell.\r\n`);
      }
  }
}

export function writePty(sessionId: string, data: string): boolean {
  const session = sessions.get(sessionId);
  if (!session) return false;
  session.pty.write(data);
  return true;
}

export function resizePty(sessionId: string, cols: number, rows: number): boolean {
  const session = sessions.get(sessionId);
  if (!session) return false;
  session.pty.resize(cols, rows);
  return true;
}

export function destroyPty(sessionId: string) {
  const session = sessions.get(sessionId);
  if (session) {
    try {
      session.pty.kill();
    } catch {}
    sessions.delete(sessionId);
  }
}

export function getPtyCwd(sessionId: string): string {
  return join(process.cwd(), 'workspaces', sessionId);
}
