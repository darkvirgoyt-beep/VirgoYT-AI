import { route, listModels, type GatewayRequest } from './AiGateway.js';

export type AiRequest = GatewayRequest;

export async function proxyAi(req: AiRequest): Promise<{ content: string; model: string; provider: string; keyConfigured: boolean }> {
  return route(req);
}

export function getAvailableModels() {
  return listModels();
}
