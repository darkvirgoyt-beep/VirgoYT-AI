// VirgoYT Multi-Agent Workforce.
// A supervisor orchestrates specialist sub-agents (Architect, Developer,
// Researcher, Security, DevOps, Business). Each specialist is a role-framed
// LLM call. The supervisor coordinates, hands off, and merges a final result.

import { proxyAi } from '../../ai/AiProxy.js';

export type WorkforceStreamEvent =
  | { type: 'wf-agent-start'; id: string; agent: string; task: string }
  | { type: 'wf-agent-done'; id: string; agent: string; output: string }
  | { type: 'wf-plan'; id: string; steps: string[] }
  | { type: 'wf-result'; id: string; summary: string }
  | { type: 'wf-error'; id: string; message: string };

type WorkforceConfig = {
  model: string;
  stream: (e: WorkforceStreamEvent) => void;
};

type AgentSpec = {
  id: string;
  name: string;
  emoji: string;
  role: string;
  prompt: (goal: string, context: string, specRole: string) => string;
};

export class Workforce {
  constructor(private config: WorkforceConfig) {}

  private agents: AgentSpec[] = [
    {
      id: 'architect',
      name: 'AI Architect',
      emoji: '🏛️',
      role: 'Systems designer. Creates complete technical plans and architecture.',
      prompt: (goal, ctx, specRole) =>
        `You are the AI Architect. ${specRole}\nGOAL: ${goal}\n${ctx ? `CONTEXT:\n${ctx}\n` : ''}Deliver a concrete, step-by-step technical architecture and component breakdown. Be specific and actionable. Keep under 300 words.`,
    },
    {
      id: 'developer',
      name: 'AI Developer',
      emoji: '👨‍💻',
      role: 'Senior software engineer. Writes production code and debugs.',
      prompt: (goal, ctx, specRole) =>
        `You are the AI Developer. ${specRole}\nGOAL: ${goal}\n${ctx ? `CONTEXT / PLAN:\n${ctx}\n` : ''}Write the actual code/implementation approach: files, functions, dependencies, key snippets. Be concrete. Keep under 350 words.`,
    },
    {
      id: 'researcher',
      name: 'AI Researcher',
      emoji: '🔬',
      role: 'Deep researcher. Gathers facts, best practices, and summarizes knowledge.',
      prompt: (goal, ctx, specRole) =>
        `You are the AI Researcher. ${specRole}\nGOAL: ${goal}\n${ctx ? `CONTEXT:\n${ctx}\n` : ''}List the key research questions, likely best-practice answers, and any risks. Be concise and factual. Keep under 250 words.`,
    },
    {
      id: 'security',
      name: 'AI Security Expert',
      emoji: '🛡️',
      role: 'Reviews for vulnerabilities and hardens the design.',
      prompt: (goal, ctx, specRole) =>
        `You are the AI Security Expert. ${specRole}\nGOAL: ${goal}\n${ctx ? `CONTEXT / PLAN:\n${ctx}\n` : ''}Identify the top 3 security risks and how to mitigate each. Be specific. Keep under 200 words.`,
    },
    {
      id: 'devops',
      name: 'AI DevOps',
      emoji: '🚀',
      role: 'Handles deployment, CI/CD, cloud infra and automation.',
      prompt: (goal, ctx, specRole) =>
        `You are the AI DevOps. ${specRole}\nGOAL: ${goal}\n${ctx ? `CONTEXT / PLAN:\n${ctx}\n` : ''}Outline the deployment path: platform, build steps, env vars, monitoring. Concrete and practical. Keep under 250 words.`,
    },
    {
      id: 'business',
      name: 'AI Business Agent',
      emoji: '💼',
      role: 'Analyzes markets and creates strategies for the product.',
      prompt: (goal, ctx, specRole) =>
        `You are the AI Business Agent. ${specRole}\nGOAL: ${goal}\n${ctx ? `CONTEXT:\n${ctx}\n` : ''}Produce a short strategy: audience, differentiation, monetization, next steps. Keep under 200 words.`,
    },
  ];

  async coordinate(goal: string, agentIds?: string[]): Promise<WorkforceStreamEvent[]> {
    const id = `wf-${Date.now()}`;
    const events: WorkforceStreamEvent[] = [];
    const emit = (e: WorkforceStreamEvent) => {
      events.push(e);
      this.config.stream(e);
    };

    emit({ type: 'wf-plan', id, steps: agentIds ?? this.agents.map((a) => a.id) });

    // 1. Architect produces the plan
    const architect = this.agents.find((a) => a.id === 'architect');
    if (architect && (!agentIds || agentIds.includes('architect'))) {
      await this.runAgent(architect, goal, '', id, emit);
    }
    const planEvent = events.find((e): e is Extract<WorkforceStreamEvent, { agent: string; output: string }> => e.type === 'wf-agent-done' && e.agent === 'architect');
    const plan = planEvent?.output ?? '';
    const ctx = `Plan:\n${plan}`;

    for (const spec of this.agents) {
      if (spec.id === 'architect') continue;
      if (agentIds && !agentIds.includes(spec.id)) continue;
      await this.runAgent(spec, goal, ctx, id, emit);
    }

    emit({
      type: 'wf-result',
      id,
      summary: `Workforce finished ${(agentIds ?? this.agents.map((a) => a.id)).length} specialists for: "${truncate(goal, 80)}". Review each specialist's output above.`,
    });
    return events;
  }

  private async runAgent(spec: AgentSpec, goal: string, ctx: string, id: string, emit: (e: WorkforceStreamEvent) => void): Promise<void> {
    emit({ type: 'wf-agent-start', id, agent: spec.id, task: goal });
    try {
      const res = await proxyAi({ model: this.config.model, prompt: spec.prompt(goal, ctx, spec.role) });
      emit({ type: 'wf-agent-done', id, agent: spec.id, output: res.content });
    } catch (e: any) {
      emit({ type: 'wf-error', id, message: `${spec.id}: ${e.message}` });
    }
  }

  list(): { id: string; name: string; emoji: string; role: string }[] {
    return this.agents.map(({ id, name, emoji, role }) => ({ id, name, emoji, role }));
  }
}

function truncate(s: string, n: number): string {
  return s.length > n ? s.slice(0, n) + '…' : s;
}