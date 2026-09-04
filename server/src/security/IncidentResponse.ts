// VirgoYT Cyber Defense — Incident-Response Runbook Generator.
// Produces a concrete, actionable runbook (markdown) for handling a security
// incident on YOUR OWN systems. Defensive only — guides containment,
// eradication and recovery, and includes a post-incident review.

export type IncidentKind = 'breach' | 'leaked-secret' | 'malware' | 'ransomware' | 'phishing' | 'ddos' | 'data-exposure';

const KIND_LABEL: Record<IncidentKind, string> = {
  breach: 'Account / service compromise',
  'leaked-secret': 'Exposed credential or API key',
  malware: 'Malware / suspicious process',
  ransomware: 'Ransomware / encryption',
  phishing: 'Phishing / credential theft',
  ddos: 'Denial of service',
  'data-exposure': 'Sensitive data exposure',
};

const PLAYBOOK: Record<IncidentKind, { contain: string[]; eradicate: string[]; recover: string[]; prove: string[] }> = {
  breach: {
    contain: ['Disconnect the affected account/service from the network', 'Revoke all sessions and rotate passwords', 'Enable MFA and check for new sessions/keys'],
    eradicate: ['Identify and quarantine the attack path (token, session, callback)', 'Remove any implanted backdoor / added OAuth app / new SSH key'],
    recover: ['Restore from last clean backup', 'Confirm no persistence survives reboot', 'Force re-auth for all users'],
    prove: ['Collect logs and timestamps', 'Record who was notified and when'],
  },
  'leaked-secret': {
    contain: ['Identify every place the secret was used or committed', 'Revoke the key immediately in the provider dashboard', 'Block the repo/commit if public'],
    eradicate: ['Rewrite git history or scrub the secret from all copies', 'Purge caches (CDN, build cache) that may hold it'],
    recover: ['Issue a fresh key and store it in a vault/env', 'Add secret scanning to CI and commit hooks'],
    prove: ['Audit access logs for unauthorized use of the key', 'Document rotation timeline'],
  },
  malware: {
    contain: ['Disconnect the infected host from the network', 'Isolate the process and note its path/hash', 'Snapshot memory and processes for analysis'],
    eradicate: ['Kill the process tree and remove artifacts', 'Run a clean antivirus/removal scan', 'Remove suspicious persistence (startup, cron, registry)'],
    recover: ['Reboot into a clean state and patch', 'Restore files from backup if damaged'],
    prove: ['Keep the malware sample in a quarantine dir (don’t open it)', 'Write up the IOCs (hash, C2, paths)'],
  },
  ransomware: {
    contain: ['Disconnect ALL affected systems immediately', 'Do NOT pay — notify law enforcement', 'Preserve evidence; unplug backups before they encrypt'],
    eradicate: ['Identify entry vector from logs', 'Wipe and reimage affected hosts from clean media'],
    recover: ['Restore from offline/immutable backups', 'Patch the exploited vulnerability before reconnect'],
    prove: ['Retain encrypted samples and ransom note for investigation', 'Engage incident-response/cyber-insurance per policy'],
  },
  phishing: {
    contain: ['Have affected users rotate passwords and enable MFA', 'Report suspicious emails (abuse / your host) and block sender', 'Check for unauthorized forwards / mail rules'],
    eradicate: ['Remove any malicious mail rules/redirects', 'Sign out all active sessions'],
    recover: ['Re-scan for other account takeovers', 'Brief the team with red flags for this campaign'],
    prove: ['Save phishing samples with headers for analysis', 'Track who clicked vs. submitted data'],
  },
  ddos: {
    contain: ['Enable rate limiting / WAF / CDN in front of the origin', 'Block offending ASNs/IP ranges at edge', 'Scale up / fail-over to absorb or deflect traffic'],
    eradicate: ['Identify botnet/C2 signatures from logs to block', 'Remove amplification vectors (open resolvers, proxies)'],
    recover: ['Return normal routing once traffic normalizes', 'Pressure-test mitigation for the next event'],
    prove: ['Capture traffic stats and pcap during the event', 'Document downtime and impact metric'],
  },
  'data-exposure': {
    contain: ['Take the exposed dataset offline / restrict access', 'Determine the scope: which records, how exposed, how long', 'Freeze accounts for affected users if credentials involved'],
    eradicate: ['Remove the misconfigured share/bucket/permission', 'Check for other similar exposures (CI/CD, backups)'],
    recover: ['Notify affected individuals and regulators per law', 'Re-issue credentials and enable monitoring for victims'],
    prove: ['Log the exposure window and access attempts', 'Build a timeline for any required disclosure'],
  },
};

export function generateRunbook(kind: IncidentKind, context: string, owner?: string): string {
  const p = PLAYBOOK[kind];
  const now = new Date().toISOString();
  const L = (l: string) => (owner ? `**${owner}** — ` : '') + `${l}`;
  return [
    `# Incident Response Runbook — ${KIND_LABEL[kind]}`,
    ``,
    `Generated: ${now}`,
    ``,
    `## Context`,
    context.trim() || '*(add what you observed: systems, scope, first detection…)*',
    ``,
    `## 1. Containment`,
    ...p.contain.map((s, i) => `${i + 1}. ${L(s)}`),
    ``,
    `## 2. Eradication`,
    ...p.eradicate.map((s, i) => `${i + 1}. ${L(s)}`),
    ``,
    `## 3. Recovery`,
    ...p.recover.map((s, i) => `${i + 1}. ${L(s)}`),
    ``,
    `## 4. Evidence & Review`,
    ...p.prove.map((s, i) => `${i + 1}. ${L(s)}`),
    ``,
    `## Post-incident checklist`,
    `- [ ] Rotate/revoke any exposed secrets`,
    `- [ ] Patch the root cause and verify`,
    `- [ ] Notify anyone required (internal, users, regulators)`,
    `- [ ] Update this runbook with lessons learned`,
    ``,
    `_Defensive guide for systems you own or are authorized to defend._`,
  ].join('\n');
}

export function runbookKinds(): { id: IncidentKind; label: string }[] {
  return Object.keys(KIND_LABEL).map((k) => ({ id: k as IncidentKind, label: KIND_LABEL[k as IncidentKind] }));
}