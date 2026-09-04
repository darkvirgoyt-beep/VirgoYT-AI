// VirgoYT Autonomous Agent Engine.
// A loop-based coding/browsing agent with real tools, live activity streaming,
// confirmation guardrails, and multi-provider LLM support.

import { proxyAi } from '../ai/AiProxy.js';
import { runCommandInSandbox } from '../sandbox/DockerManager.js';
import { readFile, writeFile, listTree, deleteEntry } from '../filesystem/FileManager.js';
import { downloadFile } from '../tools/Downloader.js';
import { browseUrl, screenshotUrl } from '../tools/Browser.js';

export type AgentStreamEvent =
  | { type: 'run-start'; goal: string; id: string }
  | { type: 'thought'; id: string; text: string }
  | { type: 'plan'; id: string; steps: string[] }
  | { type: 'tool-start'; id: string; tool: string; input: string }
  | { type: 'tool-end'; id: string; tool: string; output: string }
  | { type: 'command'; id: string; command: string; output: string }
  | { type: 'browse'; id: string; url: string; title?: string }
  | { type: 'site-preview'; id: string; url: string }
  | { type: 'download'; id: string; url: string; path: string; size: number }
  | { type: 'file-write'; id: string; path: string }
  | { type: 'confirmation-required'; id: string; action: string; reason: string }
  | { type: 'confirmation-denied'; id: string; reason: string }
  | { type: 'result'; id: string; summary: string }
  | { type: 'error'; id: string; message: string }
  | { type: 'run-end'; id: string };

type AgentConfig = {
  model: string;
  sessionId: string;
  rootDir: string;
  stream: (event: AgentStreamEvent) => void;
};

type ToolResult = { ok: boolean; output: string };

const DANGEROUS_PATTERNS =
  /(rm\s+-rf|git\s+push\s+--force|DROP\s+TABLE|TRUNCATE|dd\s+of=|:(){:|>\/dev\/sda|mkfs)/;

export class AgentEngine {
  private confirmed = new Set<string>();

  constructor(private config: AgentConfig) {}

  async run(goal: string): Promise<AgentStreamEvent[]> {
    const id = `run-${Date.now()}`;
    const events: AgentStreamEvent[] = [];
    const emit = (e: AgentStreamEvent) => {
      events.push(e);
      this.config.stream(e);
    };

    emit({ type: 'run-start', goal, id });

    try {
      // 1. Plan
      const plan = await this.createPlan(goal);
      emit({ type: 'plan', id, steps: plan });
      emit({ type: 'thought', id, text: `Planning done — ${plan.length} steps. Starting execution.` });

      // 2. Execute steps
      for (const [i, step] of plan.entries()) {
        emit({ type: 'thought', id, text: `Executing step ${i + 1}/${plan.length}: ${step}` });
        await this.executeStep(goal, step, id, emit);
      }

      // 3. Summarize
      emit({
        type: 'result',
        id,
        summary: `Finished ${plan.length} steps for: "${truncate(goal, 80)}". Open the terminal, files, or browser panel to inspect results.`,
      });
    } catch (e) {
      emit({ type: 'error', id, message: (e as Error).message });
    }

    emit({ type: 'run-end', id });
    return events;
  }

  // LLM that returns a JSON array of steps
  private async createPlan(goal: string): Promise<string[]> {
    const prompt = `You are an autonomous cloud AI computer. Plan a sequence of concrete, numbered terminal/browser/file actions to accomplish:
"${goal}"
Return ONLY a JSON array of strings, e.g. ["ls -la", "python3 --version"]. No markdown, no prose. Max 8 steps.`;
    const res = await proxyAi({ model: this.config.model, prompt });
    return this.parsePlanList(res.content);
  }

  private parsePlanList(text: string): string[] {
    try {
      const matched = text.match(/\[[\s\S]*\]/);
      if (!matched) return [text.trim()];
      const arr = JSON.parse(matched[0]);
      if (Array.isArray(arr)) return arr.map(String).slice(0, 8);
      return [text.trim()];
    } catch {
      return [text.trim()];
    }
  }

  private async executeStep(
    goal: string,
    step: string,
    runId: string,
    emit: (e: AgentStreamEvent) => void
  ): Promise<void> {
    const s = step.toLowerCase();

    // INTERPRET the natural-language step into a real command / tool call
    const toolAction = await this.interpretStep(step, goal);

    emit({ type: 'tool-start', id: runId, tool: toolAction.tool, input: step });

    switch (toolAction.tool) {
      case 'terminal': {
        const command = toolAction.command ?? step;
        if (this.needsConfirmation(command, runId)) {
          emit({ type: 'confirmation-required', id: runId, action: command, reason: 'Potentially destructive command detected.' });
          const allowed = await this.awaitConfirmation(runId, command);
          if (!allowed) {
            emit({ type: 'confirmation-denied', id: runId, reason: 'User denied dangerous command.' });
            return;
          }
        }
        emit({ type: 'command', id: runId, command, output: '…' });
        const out = await runCommandInSandbox(this.config.sessionId, command, this.config.rootDir);
        emit({ type: 'tool-end', id: runId, tool: 'terminal', output: truncate(out, 4000) });
        break;
      }

      case 'browse': {
        const url = await this.resolveUrl(toolAction.url ?? step);
        emit({ type: 'browse', id: runId, url });
        const res = await browseUrl(url);
        emit({ type: 'tool-end', id: runId, tool: 'browse', output: truncate(res.text, 6000) });
        break;
      }

      case 'preview': {
        const url = toolAction.url ?? step;
        emit({ type: 'site-preview', id: runId, url });
        const shot = await screenshotUrl(url);
        emit({ type: 'tool-end', id: runId, tool: 'preview', output: shot ? `Screenshot saved: ${shot}` : 'Preview unavailable' });
        break;
      }

      case 'download': {
        const url = await this.resolveUrl(toolAction.url ?? step);
        const dest = toolAction.dest ?? 'downloads';
        emit({ type: 'download', id: runId, url, path: dest, size: 0 });
        const result = await downloadFile(url, this.config.rootDir, dest);
        emit({ type: 'tool-end', id: runId, tool: 'download', output: `Downloaded to ${result.path} (${result.size} bytes)` });
        break;
      }

      case 'write-file': {
        const fileInfo = await this.interpretFileWrite(step);
        await writeFile(this.config.rootDir, fileInfo.path, fileInfo.content);
        emit({ type: 'file-write', id: runId, path: fileInfo.path });
        emit({ type: 'tool-end', id: runId, tool: 'write-file', output: `Wrote ${fileInfo.path}` });
        break;
      }

      case 'list': {
        const tree = await listTree(this.config.rootDir, '');
        emit({ type: 'tool-end', id: runId, tool: 'list', output: tree.map((n) => n.path).join('\n') });
        break;
      }

      default: {
        // Fall back to terminal
        const out = await runCommandInSandbox(this.config.sessionId, step, this.config.rootDir);
        emit({ type: 'tool-end', id: runId, tool: 'terminal', output: truncate(out, 4000) });
      }
    }
  }

  // Classify a natural-language step into a tool + arguments
  private async interpretStep(step: string, goal: string) {
    const s = step.toLowerCase();
    if (s.startsWith('http') || /\b(open|visit|go to|browse|search)\b/.test(s)) {
      return { tool: 'browse' as const, url: step.match(/https?:\/\/\S+/)?.[0] ?? null };
    }
    if (s.includes('preview') || s.includes('screenshot') || s.includes('see site')) {
      return { tool: 'preview' as const, url: step.match(/https?:\/\/\S+/)?.[0] ?? 'http://localhost:3000' };
    }
    if (s.includes('download') || s.includes('fetch ') || s.includes('get file') || /\.(apk|exe|png|mp4|zip)/.test(s)) {
      return { tool: 'download' as const, url: step.match(/https?:\/\/\S+/)?.[0] ?? null, dest: 'downloads' };
    }
    if (s.includes('write file') || s.includes('create file') || s.includes('save file')) {
      return { tool: 'write-file' as const };
    }
    if (s.includes('list') || s.includes('show files') || s.includes('ls')) {
      return { tool: 'list' as const };
    }
    // Default to terminal
    return { tool: 'terminal' as const, command: this.extractCommand(step) };
  }

  private extractCommand(step: string): string {
    // strip annotations like "run " / "execute "
    return step
      .replace(/^(run|execute|now|please|the|command)\s+/i, '')
      .replace(/[`"]/g, '')
      .trim();
  }

  private async interpretFileWrite(step: string): Promise<{ path: string; content: string }> {
    const prompt = `Extract a file CREATE request. Given: "${step}"
Return JSON: {"path":"relative/path.ext","content":"the file content"}. Number the code in content with \\n. No prose.`;
    const res = await proxyAi({ model: this.config.model, prompt });
    try {
      const matched = res.content.match(/\{[\s\S]*\}/);
      if (matched) {
        const parsed = JSON.parse(matched[0]);
        return { path: parsed.path ?? 'generated.txt', content: parsed.content ?? '' };
      }
    } catch {}
    return { path: 'generated.txt', content: step };
  }

  private async resolveUrl(step: string): Promise<string> {
    if (/^https?:\/\//.test(step)) return step;
    // If it looks like a query, hit a search engine
    const q = encodeURIComponent(step);
    return `https://www.google.com/search?q=${q}`;
  }

  private needsConfirmation(command: string, runId: string): boolean {
    if (this.confirmed.has(runId)) return false;
    return DANGEROUS_PATTERNS.test(command);
  }

  private awaitConfirmation(runId: string, action: string): Promise<boolean> {
    return new Promise((resolve) => {
      const timer = setTimeout(() => {
        this._pendingConfirm = null;
        resolve(false);
      }, 120000);
      this._pendingConfirm = (approve: boolean) => {
        clearTimeout(timer as any);
        this._pendingConfirm = null;
        if (approve) this.confirmed.add(runId);
        resolve(approve);
      };
    });
  }

  private _pendingConfirm: ((approve: boolean) => void) | null = null;

  confirmExternal(approve: boolean) {
    if (this._pendingConfirm) this._pendingConfirm(approve);
  }
}

function truncate(s: string, n: number): string {
  return s.length > n ? s.slice(0, n) + '\n…[truncated]' : s;
}