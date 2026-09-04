import express from 'express';
import { createServer } from 'http';
import { Server } from 'socket.io';
import cors from 'cors';
import cookieParser from 'cookie-parser';
import 'dotenv/config';

import { initDb, dbMode } from './db/Database.js';
import { sandbox, runCommandInSandbox } from './sandbox/DockerManager.js';
import { listTree, readFile, writeFile, createDirectory, deleteEntry, renameEntry } from './filesystem/FileManager.js';
import { proxyAi, getAvailableModels } from './ai/AiProxy.js';
import { env as providerEnv } from './ai/AiGateway.js';
import { loginUser, registerUser, getUserFromToken, extractToken, newSessionId, verifyToken, googleLogin, googleClientId } from './auth/AuthManager.js';
import { verifyGoogleIdToken } from './auth/GoogleVerify.js';
import { createPty, writePty, resizePty, destroyPty, setPtyCallbacks, getPtyCwd } from './terminal/PTYManager.js';
import { AgentEngine } from './agent/AgentEngine.js';
import { proxyHtml } from './tools/Browser.js';
import { McpRegistry, DEFAULT_MCP } from './mcp/McpClient.js';
import { PluginManager } from './plugins/PluginManager.js';
import { Workforce } from './agent/workforce/Workforce.js';
import { Factory } from './agent/factory/Factory.js';
import { initMemory, remember, recall, clearMemory, toPrompt, ROOT_KEY } from './agent/memory/MemoryStore.js';
import { scanWorkspace } from './security/SecurityScanner.js';
import { labs } from './security/Labs.js';
import { generateRunbook, runbookKinds } from './security/IncidentResponse.js';
import { exportProject } from './build/BuildExporter.js';
import { listConnectors, type ProviderId } from './build/DeployConnectors.js';
import { exchangeCode, storeToken, authorizedIds, hasStoredToken, clearTokens, tokenMeta } from './build/OAuth.js';

const PORT = Number(process.env.PORT ?? 8080);
const CLIENT_ORIGINS = (process.env.CLIENT_ORIGIN ?? 'https://virgo-yt-ai.vercel.app,http://localhost:3000').split(',').map((origin) => origin.trim()).filter(Boolean);

initDb().then(() => {
  console.log(`   DB backend: ${dbMode()}${dbMode() === 'postgres' ? '' : ` (file: ${process.env.DB_PATH ?? 'data/virgoyt.json'})`}`);
});

const app = express();
app.use(
  cors({
    origin: CLIENT_ORIGINS,
    credentials: true,
  })
);
app.use(express.json({ limit: '50mb' }));
app.use(cookieParser());

const httpServer = createServer(app);
const io = new Server(httpServer, {
  cors: { origin: CLIENT_ORIGINS, credentials: true },
});

const sessions = new Map<
  string,
  { userId: number | null; sandbox: { containerId: string; rootDir: string; mode: string } }
>();

setPtyCallbacks({
  onData: (sessionId, data) => {
    io.to(`term:${sessionId}`).emit('term:data', data);
  },
  onExit: (sessionId) => {
    io.to(`term:${sessionId}`).emit('term:exit');
  },
});

// ---------- REST API ----------

app.get('/api/health', (_req, res) => {
  res.json({ status: 'ok', version: '1.0.0', time: Date.now() });
});

app.post('/api/auth/register', async (req, res) => {
  try {
    const { email, password, name } = req.body as { email: string; password: string; name: string };
    if (!email || !password || !name) {
      return res.status(400).json({ error: 'Missing fields' });
    }
    const result = await registerUser(email, password, name);
    res.json(result);
  } catch (e: any) {
    res.status(e.status ?? 500).json({ error: e.message });
  }
});

app.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body as { email: string; password: string };
    if (!email || !password) return res.status(400).json({ error: 'Missing fields' });
    const result = await loginUser(email, password);
    res.json(result);
  } catch (e: any) {
    res.status(e.status ?? 500).json({ error: e.message });
  }
});

// Google Sign-In: client sends an ID token obtained from Google Identity
// Services; we verify its signature/audience server-side, then log the user in
// (auto-creating an account on first use). Never trust a client secret here.
app.post('/api/auth/google', async (req, res) => {
  try {
    const { idToken } = req.body as { idToken?: string };
    if (!idToken || typeof idToken !== 'string') {
      return res.status(400).json({ error: 'Missing idToken' });
    }
    const clientId = googleClientId();
    if (!clientId) {
      return res.status(500).json({ error: 'Google login not configured on server (set GOOGLE_CLIENT_ID)' });
    }
    const info = await verifyGoogleIdToken(idToken, clientId);
    if (!info) return res.status(401).json({ error: 'Invalid Google token' });
    const result = await googleLogin(info.email, info.name);
    res.json(result);
  } catch (e: any) {
    res.status(e.status ?? 500).json({ error: e.message });
  }
});

app.get('/api/auth/google/config', (_req, res) => {
  res.json({ enabled: Boolean(googleClientId()), clientId: googleClientId() ?? null });
});

app.get('/api/auth/me', async (req, res) => {
  const token = extractToken(req.headers.authorization);
  if (!token) return res.status(401).json({ error: 'No token' });
  const user = await getUserFromToken(token);
  if (!user) return res.status(401).json({ error: 'Invalid token' });
  res.json({ user });
});

function requireSession(req: express.Request, res: express.Response, next: express.NextFunction) {
  const token = extractToken(req.headers.authorization);
  if (!token) return res.status(401).json({ error: 'Auth required' });
  const sessionId = (req.query.sessionId as string) || (req.body?.sessionId as string);
  const existing = sessionId ? sessions.get(sessionId) : undefined;
  if (!existing) return res.status(404).json({ error: 'No active session' });
  (req as any).sessionId = sessionId;
  (req as any).sandbox = existing.sandbox;
  next();
}

app.post('/api/session/create', (req, res) => {
  const token = extractToken(req.headers.authorization);
  let userId: number | null = null;
  if (token) {
    const payload = verifyToken(token);
    userId = payload?.userId ?? null;
  }

  const sessionId = newSessionId();
  const info = sandbox.createSession(sessionId);
  sessions.set(sessionId, { userId, sandbox: info });
  io.emit('session:created', { sessionId });
  res.json({ sessionId, rootDir: info.rootDir, mode: info.mode });
});

app.post('/api/terminal/command', async (req, res) => {
  try {
    const { command } = req.body as { command: string };
    const sessionId = (req.headers['x-session-id'] as string) || '';
    const existing = sessions.get(sessionId);
    const rootDir = existing?.sandbox.rootDir ?? process.cwd();
    const output = await runCommandInSandbox(sessionId || 'anon', command, rootDir);
    res.json({ output });
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.get('/api/files/tree', requireSession, async (req, res) => {
  try {
    const rootDir = (req as any).sandbox.rootDir;
    const path = (req.query.path as string) ?? '/';
    const tree = await listTree(rootDir, path.replace(/^\/+/, ''));
    res.json({ tree });
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.get('/api/files/read', requireSession, async (req, res) => {
  try {
    const rootDir = (req as any).sandbox.rootDir;
    const path = (req.query.path as string) ?? '';
    res.json(await readFile(rootDir, path.replace(/^\/+/, '')));
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.post('/api/files/write', requireSession, async (req, res) => {
  try {
    const rootDir = (req as any).sandbox.rootDir;
    const { path, content } = req.body as { path: string; content: string };
    if (!path) return res.status(400).json({ error: 'Missing path' });
    res.json(await writeFile(rootDir, path.replace(/^\/+/, ''), content ?? ''));
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.post('/api/files/mkdir', requireSession, async (req, res) => {
  try {
    const rootDir = (req as any).sandbox.rootDir;
    const { path } = req.body as { path: string };
    res.json(await createDirectory(rootDir, path.replace(/^\/+/, '')));
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.delete('/api/files', requireSession, async (req, res) => {
  try {
    const rootDir = (req as any).sandbox.rootDir;
    const path = (req.query.path as string) ?? '';
    res.json(await deleteEntry(rootDir, path.replace(/^\/+/, '')));
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.post('/api/files/rename', requireSession, async (req, res) => {
  try {
    const rootDir = (req as any).sandbox.rootDir;
    const { from, to } = req.body as { from: string; to: string };
    res.json(await renameEntry(rootDir, from.replace(/^\/+/, ''), to.replace(/^\/+/, '')));
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.post('/api/ai/chat', async (req, res) => {
  try {
    const { model, prompt, history, system, temperature, maxTokens } = req.body as any;
    if (!prompt) return res.status(400).json({ error: 'Missing prompt' });
    const result = await proxyAi({ model, prompt, history, system, temperature, maxTokens });
    res.json(result);
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.get('/api/ai/models', (_req, res) => {
  res.json({ models: getAvailableModels() });
});

app.get('/api/ai/config', (_req, res) => {
  res.json({
    configured: Object.fromEntries(
      Object.entries(providerEnv).map(([k, v]) => [k, Boolean(v)])
    ),
    googleScript: Boolean(process.env.GOOGLE_SCRIPT_URL),
  });
});

app.get('/api/stats', (req, res) => {
  const sessionId = (req.query.sessionId as string) || '';
  const info = sessions.get(sessionId);
  res.json(info ? sandbox.stats(sessionId) : {});
});

// ---------- Autonomous Agent ----------

const activeAgents = new Map<string, AgentEngine>();

app.post('/api/agent/run', async (req, res) => {
  try {
    const { goal = '', sessionId = '', model = 'auto' } = req.body as any;
    if (!goal) return res.status(400).json({ error: 'Missing goal' });
    const s = sessions.get(sessionId);
    if (!s) {
      const info = sandbox.createSession(sessionId);
      sessions.set(sessionId, { userId: null, sandbox: info as any });
    }
    const entry = sessions.get(sessionId)!;
    const rootDir = entry.sandbox.rootDir;

    const events: any[] = [];
    const agent = new AgentEngine({
      model,
      sessionId,
      rootDir,
      mcp: mcpRegistry,
      plugins: pluginManager,
      memoryContext: toPrompt(sessionId),
      toolDescribe:
        ['MCP tools:', ...pluginManager.all().flatMap((p) => p.tools.map((t) => `- plugin:${t.name}: ${t.description}`)), mcpRegistry.serversConnected().length ? '(MCP available via mcp:<server>:<tool>)' : ''].join('\n') ||
        undefined,
      stream: (e) => {
        events.push(e);
        io.to(`run:${sessionId}`).emit('agent:event', e);
      },
    });
    activeAgents.set(sessionId, agent);
    remember(sessionId, 'project', 'goal', truncateGoal(goal));
    res.json({ ok: true, runId: `run-${Date.now()}` });
    void agent.run(goal).finally(() => activeAgents.delete(sessionId));
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.post('/api/agent/confirm', (req, res) => {
  const { sessionId, approve } = req.body as any;
  const agent = activeAgents.get(sessionId);
  if (!agent) return res.status(404).json({ error: 'No active agent' });
  agent.confirmExternal(Boolean(approve));
  res.json({ ok: true });
});

app.get('/api/browser/preview', async (req, res) => {
  try {
    const url = (req.query.url as string) ?? '';
    if (!/^https?:\/\//.test(url)) return res.status(400).json({ error: 'Invalid URL' });
    const html = await proxyHtml(url);
    res.type('html').send(html);
  } catch (e: any) {
    res.status(502).json({ error: e.message });
  }
});

// ---------- Socket.IO ----------

io.on('connection', (socket) => {
  let sessionId = '';
  const querySession = socket.handshake.query.sessionId as string;

  socket.on('terminal:start', (data) => {
    const sid = (data?.sessionId ?? querySession) as string;
    const existing = sessions.get(sid);
    if (!existing) {
      const info = sandbox.createSession(sid);
      sessions.set(sid, { userId: null, sandbox: info });
    }
    sessionId = sid;
    socket.join(`term:${sid}`);
    const cwd = getPtyCwd(sid);
    createPty(sid, cwd);
    socket.emit('terminal:ready', { sessionId: sid });
  });

  socket.on('terminal:input', (data) => {
    writePty(data.sessionId ?? sessionId, data.data);
  });

  socket.on('terminal:resize', (data) => {
    resizePty(data.sessionId ?? sessionId, data.cols, data.rows);
  });

  socket.on('terminal:stop', (data) => {
    destroyPty(data.sessionId ?? sessionId);
  });

  socket.on('agent:watch', (data) => {
    const sid = data?.sessionId ?? querySession;
    if (sid) {
      socket.join(`run:${sid}`);
      socket.join(`wf:${sid}`);
      socket.join(`fx:${sid}`);
    }
  });

  socket.on('agent:confirm', (data) => {
    const { sessionId: sid, approve } = data ?? {};
    const agent = activeAgents.get(sid ?? sessionId);
    if (agent) agent.confirmExternal(Boolean(approve));
  });

  socket.on('disconnect', () => {
    if (sessionId) {
      destroyPty(sessionId);
    }
  });
});

// ---------- MCP + Plugins ----------

const pluginManager = new PluginManager();
const defaultSandbox = sandbox.createSession('mcp-boot');
const mcpRegistry = new McpRegistry(DEFAULT_MCP(defaultSandbox.rootDir));

async function bootIntegrations() {
  initMemory();
  await pluginManager.loadAll();
  void mcpRegistry.startAll();
  const connected = mcpRegistry.serversConnected();
  console.log(`   Memory: enabled`);
  console.log(`   Plugins: ${pluginManager.all().length} loaded`);
  console.log(`   MCP: ${connected.length ? connected.join(', ') : 'none connected (install via npx)'}`);
}
void bootIntegrations();

app.get('/api/agent/capabilities', async (_req, res) => {
  let mcpTools: { server: string; tool: string; description?: string }[] = [];
  try {
    mcpTools = (await mcpRegistry.allTools()).map(({ server, tool }) => ({
      server,
      tool: tool.name,
      description: tool.description,
    }));
  } catch {}
  res.json({
    mcp: { servers: mcpRegistry.serversConnected(), tools: mcpTools },
    plugins: pluginManager.all().map((p) => ({ id: p.id, name: p.name, version: p.version, description: p.description, tools: p.tools.map((t) => t.name) })),
  });
});

// ---------- Workforce ----------

app.get('/api/agent/roster', (_req, res) => {
  const workforce = new Workforce({ model: 'auto', stream: () => {} });
  res.json({ agents: workforce.list() });
});

app.post('/api/agent/workforce', async (req, res) => {
  try {
    const { goal = '', sessionId = '', model = 'auto', agents } = req.body as any;
    if (!goal) return res.status(400).json({ error: 'Missing goal' });
    const stream = (e: any) => io.to(`wf:${sessionId}`).emit('workforce:event', e);
    const workforce = new Workforce({ model, stream });
    res.json({ ok: true, runId: `wf-${Date.now()}` });
    void workforce.coordinate(goal, agents);
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

// ---------- Factory Mode ----------

app.post('/api/agent/factory', async (req, res) => {
  try {
    const { idea = '', sessionId = '', model = 'auto' } = req.body as any;
    if (!idea) return res.status(400).json({ error: 'Missing idea' });
    const s = sessions.get(sessionId);
    if (!s) {
      const info = sandbox.createSession(sessionId);
      sessions.set(sessionId, { userId: null, sandbox: info as any });
    }
    const entry = sessions.get(sessionId)!;
    const stream = (e: any) => io.to(`fx:${sessionId}`).emit('factory:event', e);
    const factory = new Factory({ model, sessionId, rootDir: entry.sandbox.rootDir, stream });
    res.json({ ok: true, runId: `fx-${Date.now()}` });
    void factory.run(idea);
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

// ---------- Memory ----------

app.get('/api/agent/memory', (req, res) => {
  const sessionId = (req.query.sessionId as string) || '';
  res.json({ memory: recall(sessionId) });
});

app.post('/api/agent/memory', (req, res) => {
  const { sessionId = '', kind = 'fact', name = '', value = '' } = req.body as any;
  if (!name || !value) return res.status(400).json({ error: 'name and value required' });
  remember(sessionId, kind, name, value);
  res.json({ ok: true });
});

app.delete('/api/agent/memory', (req, res) => {
  const sessionId = (req.query.sessionId as string) || '';
  clearMemory(sessionId);
  res.json({ ok: true });
});

// ---------- Skills ----------

app.get('/api/agent/skills', (_req, res) => {
  res.json({
    skills: pluginManager.all().map((p) => ({ id: p.id, name: p.name, version: p.version, description: p.description, tools: p.tools.map((t) => t.name) })),
    install: 'Drop a folder under server/plugins/ with plugin.json (+ optional handler) to add a skill.',
  });
});

// ---------- Cyber Defense (analyze your own code) ----------

app.get('/api/cyber/labs', (req, res) => {
  res.json({ labs: labs(req.query.q as string | undefined) });
});

app.post('/api/cyber/scan', async (req, res) => {
  try {
    const { sessionId = '' } = req.body as any;
    const entry = sessions.get(sessionId);
    const root = entry?.sandbox.rootDir;
    if (!root) return res.status(400).json({ error: 'No active workspace for this session' });
    const report = await scanWorkspace(root);
    res.json(report);
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

app.get('/api/cyber/runbook/kinds', (_req, res) => {
  res.json({ kinds: runbookKinds() });
});

app.post('/api/cyber/runbook', (req, res) => {
  try {
    const { kind = 'breach', context = '', owner = '' } = req.body as any;
    if (!['breach', 'leaked-secret', 'malware', 'ransomware', 'phishing', 'ddos', 'data-exposure'].includes(kind)) {
      return res.status(400).json({ error: 'kind must be one of the runbook kinds' });
    }
    res.json({ markdown: generateRunbook(kind, context, owner), kind, generatedAt: Date.now() });
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

// ---------- Build & Deploy ----------

app.get('/api/build/connectors', (_req, res) => {
  res.json({ connectors: listConnectors(authorizedIds()) });
});

app.get('/api/build/oauth/status', (_req, res) => {
  const ids = authorizedIds();
  res.json({
    authorized: ids,
    meta: Object.fromEntries(ids.map((id: ProviderId) => [id, tokenMeta(id)])),
    secretScanning: false,
  });
});

app.post('/api/build/oauth/revoke', (req, res) => {
  clearTokens();
  res.json({ ok: true });
});

app.get('/api/build/oauth/callback', async (req, res) => {
  const provider = req.query.provider as string | undefined;
  const code = req.query.code as string | undefined;
  const error = req.query.error as string | undefined;
  res.setHeader('Content-Type', 'text/html; charset=utf-8');
  if (error) {
    return res.send(`<h3>Authorization failed</h3><p>${escapeHtml(error)}</p><p>You can close this tab and return to Virgo.</p>`);
  }
  if (!provider || !code) return res.status(400).send('<h3>Missing provider or code</h3>');
  if (!['github', 'vercel', 'supabase', 'render'].includes(provider)) return res.status(400).send('<h3>Unknown provider</h3>');
  try {
    const pid = provider as any as ProviderId;
    const token = await exchangeCode(pid, code);
    storeToken(pid, token);
    res.send(`<h3>✓ ${escapeHtml(provider)} authorized</h3><p>You can close this tab and return to Virgo — your workspace can now deploy to ${escapeHtml(provider)}.</p>`);
  } catch (e: any) {
    res.status(400).send(`<h3>Could not authorize ${escapeHtml(provider)}</h3><p>${escapeHtml(e.message)}</p>`);
  }
});

app.post('/api/build/export', async (req, res) => {
  try {
    const { sessionId = '', target = 'web', appName = 'virgoyt-app' } = req.body as any;
    if (!['web', 'exe', 'apk', 'mac', 'terminal'].includes(target)) {
      return res.status(400).json({ error: 'target must be web|exe|apk|mac|terminal' });
    }
    const entry = sessions.get(sessionId);
    const root = entry?.sandbox.rootDir;
    if (!root) return res.status(400).json({ error: 'No active workspace for this session' });
    const result = await exportProject(root, target, appName);
    res.json(result);
  } catch (e: any) {
    res.status(500).json({ error: e.message });
  }
});

httpServer.listen(PORT, () => {
  console.log(`\n⚡ VirgoYT Cloud AI server`);
  console.log(`   REST API:  http://localhost:${PORT}/api`);
  console.log(`   Websocket: ws://localhost:${PORT}/\n`);
});

function escapeHtml(s: string): string {
  return s.replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c] as string));
}

function truncateGoal(s: string, n = 140): string {
  return s.length > n ? s.slice(0, n) + '…' : s;
}
