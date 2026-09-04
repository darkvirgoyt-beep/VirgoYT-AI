// VirgoYT Cyber Defense — Ethical Hacking Lab Catalog.
// Defensive & sanctioned education ONLY: teaches how attacks work so you can
// defend, using legal, dedicated training environments (sandboxes, CTFs,
// vulnerable-by-design apps). No tooling for attacking real third-party systems.

export type Lab = {
  id: string;
  title: string;
  focus: string;
  environment: string;
  url?: string;
  caution: string;
};

export const LABS: Lab[] = [
  {
    id: 'owasp-top10',
    title: 'OWASP Top 10 Deep-Dive',
    focus: 'Understand the top web vulnerabilities (injection, XSS, SSRF, IDOR) to defend your own apps.',
    environment: 'Your own code + OWASP Juice Shop (local, legal sandbox)',
    url: 'https://owasp.org/www-project-top-ten/',
    caution: 'Practice only against your own apps or sanctioned labs.',
  },
  {
    id: 'dependency-audit',
    title: 'Dependency & Supply-Chain Audit',
    focus: 'Audit your own project deps for known CVEs and lockfiles; harden your supply chain.',
    environment: 'Local scanning of your repository',
    caution: 'Scan software you own or are authorized to test.',
  },
  {
    id: 'secret-hygiene',
    title: 'Secrets & Leak Prevention',
    focus: 'Find and rotate leaked keys, add hooks/scanning, use vaults and env isolation.',
    environment: 'Your repos + CI secret scanning',
    caution: 'Rotate, don’t publish — never post others’ tokens.',
  },
  {
    id: 'network-fundamentals',
    title: 'Network Recon & Defense Fundamentals',
    focus: 'Read traffic and understand hosts on networks YOU administer (home lab, sandbox).',
    environment: 'Localhost / your own NAT / licensed test ranges',
    caution: 'Only networks you own or are authorized to test.',
  },
  {
    id: 'social-engineering',
    title: 'Social Engineering Awareness',
    focus: 'Recognize phishing/pretexting so you and your team don’t get tricked.',
    environment: 'Awareness training, simulated phishing (own org)',
    caution: 'For defense and education, not for deceiving individuals.',
  },
  {
    id: 'incident-response',
    title: 'Incident Response & Recovery',
    focus: 'Contain, eradicate and recover from breaches on systems you manage.',
    environment: 'Your infrastructure + tabletop exercises',
    caution: 'Perform on your own assets under a clear plan.',
  },
];

export function labs(filter?: string): Lab[] {
  if (!filter) return LABS;
  const q = filter.toLowerCase();
  return LABS.filter(
    (l) => l.title.toLowerCase().includes(q) || l.focus.toLowerCase().includes(q) || l.id.includes(q)
  );
}