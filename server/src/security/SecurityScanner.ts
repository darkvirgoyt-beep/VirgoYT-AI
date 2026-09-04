// VirgoYT Cyber Defense — Security Scanner.
// Scans the USER'S OWN workspace/code for:
//   1. Exposed secrets / API tokens (so they don't leak)
//   2. Known-vulnerable dependencies (OWASP-style audit)
//   3. Weak config pointers -> hardening recommendations
// Produces a prioritized report. Defensive only — analyzes code the user owns.

import { readdirSync, readFileSync } from 'fs';
import { join, extname, basename, resolve } from 'path';

export type Finding = {
  severity: 'critical' | 'high' | 'medium' | 'low' | 'info';
  category: 'secret' | 'dependency' | 'config' | 'hardening';
  title: string;
  detail: string;
  file?: string;
  line?: number;
};

export type ScanReport = {
  scanned: number;
  findings: Finding[];
  summary: string;
  generatedAt: number;
};

const SECRET_PATTERNS: { name: string; re: RegExp }[] = [
  { name: 'GitHub token (ghp_)', re: /ghp_[A-Za-z0-9]{36,}/ },
  { name: 'OpenAI key (sk-)', re: /sk-[A-Za-z0-9]{20,}/ },
  { name: 'AWS key (AKIA)', re: /AKIA[0-9A-Z]{16}/ },
  { name: 'Private key (RSA/DSA/EC)', re: /-----BEGIN (RSA |EC |DSA )?PRIVATE KEY-----/ },
  { name: 'Slack token', re: /xox[baprs]-[A-Za-z0-9-]{10,}/ },
  { name: 'Google API key', re: /AIza[0-9A-Za-z_-]{35}/ },
  { name: 'Stripe key (sk_live)', re: /(sk|rk)_live_[0-9a-zA-Z]{20,}/ },
  { name: 'Generic Bearer token', re: /Bearer\s+[A-Za-z0-9._~+/=-]{20,}/i },
];

// Known-vulnerable example packages with CVE references (educational). 
const VULN_DEP_MARKERS: { pkg: string; cve: string; note: string }[] = [
  { pkg: 'lodash', cve: 'CVE-2021-23337', note: '>4.17.20 patches command-injection in template' },
  { pkg: 'node-fetch', cve: 'GHSA-2021', note: 'check installed version' },
  { pkg: 'tar', cve: 'CVE-2021-32458', note: 'symlink traversal' },
  { pkg: 'micromatch', cve: 'CVE-2021-21332', note: 'regex DoS' },
];

const IGNORED_DIRS = ['node_modules', '.git', 'dist', 'build', '.next', '.cache', 'workspaces', '.github'];
const BINARY_EXT = new Set(['.png', '.jpg', '.jpeg', '.gif', '.mp4', '.zip', '.exe', '.apk', '.pdf', '.woff', '.ttf', '.ico']);

export async function scanWorkspace(root: string): Promise<ScanReport> {
  const findings: Finding[] = [];
  let scanned = 0;
  const files: string[] = [];
  await collectFiles(resolve(root), files);
  scanned = files.length;

  for (const file of files) {
    scanFile(file, root, findings);
  }
  auditManifest(root, findings);
  if (!findings.length) findings.push(makeClean());

  return {
    scanned,
    findings: findings.sort((a, b) => severity(a.severity) - severity(b.severity)),
    summary: summarize(findings),
    generatedAt: Date.now(),
  };
}

async function collectFiles(dir: string, out: string[], depth = 0): Promise<void> {
  if (depth > 8) return;
  let entries: any[] = [];
  try {
    entries = readdirSync(dir, { withFileTypes: true });
  } catch {
    return;
  }
  for (const e of entries) {
    if (IGNORED_DIRS.includes(e.name)) continue;
    const p = join(dir, e.name);
    if (e.isDirectory()) await collectFiles(p, out, depth + 1);
    else if (isScannable(e.name)) out.push(p);
  }
}

function isScannable(name: string): boolean {
  const ext = extname(name).toLowerCase();
  if (BINARY_EXT.has(ext)) return false;
  if (name === 'package-lock.json' || name === 'package.json' || name === 'requirements.txt' || name === 'Pipfile') return true;
  return true;
}

function scanFile(absPath: string, root: string, findings: Finding[]): void {
  const path = absPath.replace(root, '').replace(/^\/+/, '');
  const base = basename(path).toLowerCase();
  if (base === 'package-lock.json') return;
  let content: string;
  try {
    content = readFileSafe(absPath);
  } catch {
    return;
  }
  if (content.length > 500_000) return;

  // Secrets scan
  for (const p of SECRET_PATTERNS) {
    const match = content.match(p.re);
    if (match) {
      const line = lineOf(content, match.index ?? 0);
      const redacted = redact(match[0]);
      findings.push({
        severity: 'critical',
        category: 'secret',
        title: `Possible ${p.name} exposed`,
        detail: `Found "${redacted}" — rotate immediately and move to environment variables / a vault.`,
        file: path,
        line,
      });
    }
  }

  // Common config smells -> hardening
  if (base === '.env' || base.endsWith('.env.local')) {
    const lines = content.split('\n');
    lines.forEach((l, i) => {
      if (/=\s*['"]?[A-Za-z0-9_\-]{20,}/.test(l) && !/^#/.test(l.trim())) {
        findings.push({
          severity: 'medium',
          category: 'hardening',
          title: '.env committed / present in repo',
          detail: `Line ${i + 1} looks like a secret. Ensure .env is git-ignored and keys are server-side.`,
          file: path,
          line: i + 1,
        });
      }
    });
  }
}

function auditManifest(root: string, findings: Finding[]): void {
  for (const dep of VULN_DEP_MARKERS) {
    const hit = checkDep(root, dep.pkg);
    const detail = `${dep.pkg} matches a known issue (${dep.cve}): ${dep.note}`;
    if (hit !== undefined) {
      findings.push({
        severity: 'high',
        category: 'dependency',
        title: `Potentially vulnerable dependency: ${dep.pkg}`,
        detail: `Found in ${hit}. ${detail}.`,
        file: hit,
      });
    }
  }
}

function checkDep(root: string, pkg: string): string | undefined {
  const candidates = [join(root, 'package.json'), join(root, 'requirements.txt')];
  for (const f of candidates) {
    try {
      const content = readFileSafe(f);
      const re = new RegExp(`["']?${pkg}["']?\\s*[:=~>]`);
      if (re.test(content)) return f.replace(root, '').replace(/^\/+/, '');
    } catch {}
  }
  return undefined;
}

function makeClean(): Finding {
  return {
    severity: 'info',
    category: 'hardening',
    title: 'No obvious secrets or flagged deps found',
    detail: 'Keep running scans on every change; enable secret scanning in your host and rotate keys periodically.',
  };
}

function summarize(findings: Finding[]): string {
  const c = findings.filter((f) => f.severity === 'critical').length;
  const h = findings.filter((f) => f.severity === 'high').length;
  const m = findings.filter((f) => f.severity === 'medium').length;
  if (c + h === 0) return `Clean(ish): ${m} medium/low items to review.`;
  return `Found ${c} critical, ${h} high, ${m} medium — fix critical/high first, then rotate secrets.`;
}

function severity(s: Finding['severity']): number {
  return { info: 4, low: 3, medium: 2, high: 1, critical: 0 }[s];
}

function lineOf(content: string, index: number): number {
  return content.slice(0, index).split('\n').length;
}

function redact(s: string): string {
  return s.length > 12 ? s.slice(0, 6) + '…' + s.slice(-4) : '…';
}

function readFileSafe(path: string): string {
  try {
    return readFileSync(path, 'utf8') ?? '';
  } catch {
    return '';
  }
}