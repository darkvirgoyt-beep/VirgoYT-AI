// Lightweight MCP (Model Context Protocol) client.
// Speaks JSON-RPC 2.0 over stdio transport to any MCP server process.
// Lets the Virgo agent call MCP tools (filesystem, github, fetch, docker, etc.)

import { spawn, type ChildProcess } from 'child_process';
import { once } from 'events';
import { resolve } from 'path';

export type McpTool = {
  name: string;
  description?: string;
  inputSchema?: Record<string, any>;
};

type Pending = { resolve: (v: any) => void; reject: (e: Error) => void };

export class McpClient {
  private proc: ChildProcess | null = null;
  private buf = '';
  private seq = 0;
  private pending = new Map<string, Pending>();

  constructor(
    private id: string,
    private command: string,
    private args: string[] = []
  ) {}

  get name() {
    return this.id;
  }

  async connect(): Promise<void> {
    this.proc = spawn(this.command, this.args, {
      stdio: ['pipe', 'pipe', 'pipe'],
    });
    const proc = this.proc;
    proc.stdout!.setEncoding('utf8');
    proc.stdout!.on('data', (chunk: string) => this.onData(chunk));
    proc.stderr!.setEncoding('utf8');
    proc.stderr!.on('data', (chunk: string) => this.onData(chunk, true));
    proc.on('error', (e) => this.settleAll(e));
    proc.on('exit', () => this.settleAll(new Error(`MCP server "${this.id}" exited`)));

    await this.request('initialize', {
      protocolVersion: '2024-11-05',
      capabilities: {},
      clientInfo: { name: 'virgoyt-agent', version: '1.0.0' },
    });
    // notifications/initialized
    this.notify('notifications/initialized', {});
  }

  async listTools(): Promise<McpTool[]> {
    const res = await this.request('tools/list', {});
    return (res?.tools ?? []) as McpTool[];
  }

  async callTool(name: string, args: Record<string, any>): Promise<any> {
    const res = await this.request('tools/call', { name, arguments: args });
    return res;
  }

  async close(): Promise<void> {
    if (this.proc) {
      this.proc.kill();
      this.proc = null;
    }
    this.settleAll(new Error('MCP client closed'));
  }

  // ---- internals ----

  private request(method: string, params: any): Promise<any> {
    const id = String(++this.seq);
    return new Promise((resolveP, rejectP) => {
      this.pending.set(id, { resolve: resolveP, reject: rejectP });
      this.write({ jsonrpc: '2.0', id, method, params });
    });
  }

  private notify(method: string, params: any): void {
    this.write({ jsonrpc: '2.0', method, params });
  }

  private write(msg: any): void {
    if (this.proc?.stdin?.writable) {
      this.proc.stdin.write(JSON.stringify(msg) + '\n');
    }
  }

  private onData(chunk: string, isStderr = false): void {
    if (isStderr) return;
    this.buf += chunk;
    let idx: number;
    while ((idx = this.buf.indexOf('\n')) >= 0) {
      const line = this.buf.slice(0, idx).trim();
      this.buf = this.buf.slice(idx + 1);
      if (!line) continue;
      try {
        const msg = JSON.parse(line);
        if (msg.id && this.pending.has(String(msg.id))) {
          const p = this.pending.get(String(msg.id))!;
          this.pending.delete(String(msg.id));
          if (msg.error) p.reject(new Error(msg.error.message ?? 'MCP error'));
          else p.resolve(msg.result);
        }
      } catch {
        /* ignore malformed */
      }
    }
  }

  private settleAll(err: Error): void {
    for (const [, p] of this.pending) p.reject(err);
    this.pending.clear();
  }
}

export class McpRegistry {
  private clients = new Map<string, McpClient>();

  constructor(private servers: McpServerSpec[] = []) {}

  async startAll(): Promise<void> {
    for (const spec of this.servers) {
      try {
        const cmd = spec.command.includes('/') || spec.command.includes('\\') ? resolve(spec.command) : spec.command;
        const client = new McpClient(spec.id, cmd, spec.args ?? []);
        await client.connect();
        this.clients.set(spec.id, client);
      } catch (e) {
        console.warn(`[-] MCP server "${spec.id}" failed to start: ${(e as Error).message}`);
      }
    }
  }

  async allTools(): Promise<{ server: string; tool: McpTool }[]> {
    const out: { server: string; tool: McpTool }[] = [];
    for (const [id, client] of this.clients) {
      for (const tool of await client.listTools()) {
        out.push({ server: id, tool });
      }
    }
    return out;
  }

  async call(server: string, tool: string, args: Record<string, any>): Promise<any> {
    const client = this.clients.get(server);
    if (!client) throw new Error(`MCP server "${server}" not connected`);
    return client.callTool(tool, args);
  }

  serversConnected(): string[] {
    return [...this.clients.keys()];
  }
}

export type McpServerSpec = {
  id: string;
  command: string;
  args?: string[];
};

export const DEFAULT_MCP = (workspaceDir: string): McpServerSpec[] => [
  {
    id: 'filesystem',
    command: 'npx',
    args: ['-y', '@modelcontextprotocol/server-filesystem', workspaceDir],
  },
];