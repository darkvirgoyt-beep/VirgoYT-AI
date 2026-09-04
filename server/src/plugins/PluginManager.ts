// VirgoYT plugin system.
// Plugins are folders under plugins/ containing plugin.json + optional handler code.
// Each plugin can register MCP-like tools the agent can invoke, plus a system-prompt
// snippet so the LLM knows how/when to call them.

import { readdirSync, existsSync, readFileSync } from 'fs';
import { join, resolve } from 'path';
import { pathToFileURL } from 'url';

export type PluginTool = {
  name: string;
  description: string;
  inputSchema?: Record<string, any>;
  handler: (args: Record<string, any>) => Promise<string>;
};

export type Plugin = {
  id: string;
  name: string;
  version: string;
  description: string;
  tools: PluginTool[];
  systemPrompt: string;
  dir: string;
};

export class PluginManager {
  private plugins = new Map<string, Plugin>();

  constructor(private dir = resolve(process.cwd(), 'plugins')) {}

  async loadAll(): Promise<void> {
    if (!existsSync(this.dir)) return;
    for (const folder of readdirSync(this.dir, { withFileTypes: true })) {
      if (!folder.isDirectory()) continue;
      const dir = join(this.dir, folder.name);
      const manifestPath = join(dir, 'plugin.json');
      if (!existsSync(manifestPath)) continue;
      try {
        const manifest = JSON.parse(readFileSync(manifestPath, 'utf8'));
        const loaderPath = join(dir, manifest.handler ?? 'index.mjs');
        const tools = await this.loadTools(loaderPath, manifest.tools ?? []);
        const systemPrompt = manifest.systemPrompt ?? '';
        this.plugins.set(manifest.id, {
          id: manifest.id,
          name: manifest.name ?? manifest.id,
          version: manifest.version ?? '1.0.0',
          description: manifest.description ?? '',
          tools,
          systemPrompt,
          dir,
        });
      } catch (e) {
        console.warn(`[-] Failed to load plugin in ${dir}: ${(e as Error).message}`);
      }
    }
  }

  private async loadTools(loaderPath: string, toolDefs: any[]): Promise<PluginTool[]> {
    let handlerModule: any = {};
    if (existsSync(loaderPath)) {
      try {
        handlerModule = await import(pathToFileURL(loaderPath).href);
      } catch (e) {
        console.warn(`  plugin handler import failed: ${(e as Error).message}`);
      }
    }
    return toolDefs.map((def) => ({
      name: def.name,
      description: def.description ?? '',
      inputSchema: def.inputSchema ?? {},
      handler: handlerModule[def.name]
        ? handlerModule[def.name]
        : async (args) => `Plugin tool "${def.name}" has no handler. Args: ${JSON.stringify(args)}`,
    }));
  }

  all(): Plugin[] {
    return [...this.plugins.values()];
  }

  systemPrompts(): string {
    return [...this.plugins.values()]
      .map((p) => `${p.systemPrompt}${p.tools.length ? `\nAvailable tools: ${p.tools.map((t) => t.name).join(', ')}` : ''}`)
      .join('\n\n');
  }

  findTool(name: string): { plugin: Plugin; tool: PluginTool } | null {
    for (const p of this.plugins.values()) {
      const tool = p.tools.find((t) => t.name === name);
      if (tool) return { plugin: p, tool };
    }
    return null;
  }

  toolDescriptions(): string {
    const lines: string[] = [];
    for (const p of [...this.plugins.values()]) {
      for (const t of p.tools) {
        lines.push(`- ${t.name}: ${t.description}`);
      }
    }
    return lines.join('\n');
  }
}