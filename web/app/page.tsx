import Link from 'next/link';
import { Sparkles, Code2, TerminalSquare, Globe, MonitorSmartphone, Cloud, Boxes, GitBranch, Rocket, ArrowRight } from 'lucide-react';
import { APP_NAME, LIVE_URL, REPO_URL } from '@/lib/constants';

const features = [
  {
    icon: Cloud,
    title: 'Cloud Computer',
    desc: 'A real Linux sandbox with shell, filesystem, and package manager running in your browser.',
    color: '#00d4ff',
  },
  {
    icon: Code2,
    title: 'AI Code Editor',
    desc: 'Monaco-powered editor with 50+ languages, real-time sync, and AI-assisted coding.',
    color: '#3375ff',
  },
  {
    icon: TerminalSquare,
    title: 'Live Terminal',
    desc: 'Full bash terminal with Node, Python, Git, and more. Install packages, compile, run servers.',
    color: '#00ff9c',
  },
  {
    icon: Globe,
    title: 'Browser Sandbox',
    desc: 'Test your apps in a cloud browser with responsive viewport modes.',
    color: '#b967ff',
  },
  {
    icon: MonitorSmartphone,
    title: 'Multi-Platform',
    desc: 'Run on web, desktop, mobile, and CLI. Your workspace follows you everywhere.',
    color: '#ff5c7a',
  },
  {
    icon: Boxes,
    title: 'Agent Swarm',
    desc: 'A team of 15 specialized AI agents: architect, coder, reviewer, QA, and DevOps.',
    color: '#ffb547',
  },
  {
    icon: GitBranch,
    title: 'Git & Deploy',
    desc: 'Built-in Git integration with one-click deploy to GitHub Pages.',
    color: '#00d4ff',
  },
  {
    icon: Rocket,
    title: 'Build Anything',
    desc: 'Web apps, APIs, databases, games, and bots — generated and compiled directly.',
    color: '#3375ff',
  },
];

export default function HomePage() {
  return (
    <main className="relative min-h-screen overflow-x-hidden">
      {/* Navbar */}
      <nav className="sticky top-0 z-50 backdrop-blur-xl bg-void-950/60 border-b border-white/5">
        <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="h-8 w-8 rounded-lg bg-gradient-to-br from-virgo-500 to-terminal-purple flex items-center justify-center">
              <span className="text-white font-bold text-sm">V</span>
            </div>
            <span className="font-semibold accent-gradient-text">{APP_NAME}</span>
          </div>
          <div className="hidden md:flex items-center gap-6 text-sm text-gray-400">
            <a href="#features" className="hover:text-white transition-colors">Features</a>
            <a href="#workspace" className="hover:text-white transition-colors">Workspace</a>
            <a href={LIVE_URL} target="_blank" rel="noreferrer" className="hover:text-white transition-colors">Live Demo</a>
            <a href={REPO_URL} target="_blank" rel="noreferrer" className="hover:text-white transition-colors">GitHub</a>
          </div>
          <Link
            href="/workspace"
            className="px-4 py-2 rounded-lg bg-gradient-to-r from-virgo-600 to-terminal-purple text-white font-medium text-sm hover:shadow-lg hover:shadow-virgo-600/30 transition-shadow"
          >
            Launch Workspace
          </Link>
        </div>
      </nav>

      {/* Hero */}
      <section className="relative max-w-6xl mx-auto px-6 py-24 text-center">
        <div className="absolute inset-0 -z-10 overflow-hidden pointer-events-none">
          <div className="absolute top-10 left-1/2 -translate-x-1/2 w-[700px] h-[700px] rounded-full bg-virgo-600/20 blur-[120px]" />
          <div className="absolute bottom-0 left-0 w-[500px] h-[500px] rounded-full bg-terminal-purple/10 blur-[100px]" />
        </div>

        <span className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/5 border border-white/10 text-xs text-virgo-200 mb-8">
          <span className="status-dot-online bg-terminal-green inline-block" style={{ width: 8, height: 8 }} />
          Autonomous Cloud AI Computer
        </span>

        <h1 className="text-5xl md:text-7xl font-bold leading-tight mb-6">
          <span className="bg-gradient-to-r from-white via-virgo-200 to-virgo-400 bg-clip-text text-transparent">
            Your AI Cloud Computer
          </span>
          <br />
          <span className="accent-gradient-text">in the Browser</span>
        </h1>

        <p className="max-w-2xl mx-auto text-lg text-gray-400 mb-10 leading-relaxed">
          Write code, run a real Linux terminal, compile applications, and browse the web —
          all driven by an AI that builds for you, right inside a holographic 3D workspace.
        </p>

        <div className="flex flex-wrap items-center justify-center gap-4">
          <Link
            href="/workspace"
            className="group px-8 py-4 rounded-2xl bg-gradient-to-r from-virgo-600 to-terminal-purple text-white font-semibold text-lg flex items-center gap-2 hover:shadow-2xl hover:shadow-virgo-600/40 transition-all"
          >
            Launch Cloud Workspace
            <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
          </Link>
          <a
            href="#features"
            className="px-8 py-4 rounded-2xl bg-white/5 border border-white/10 text-gray-200 font-semibold text-lg hover:bg-white/10 transition-colors"
          >
            Explore Features
          </a>
        </div>

        <div className="mt-12 grid grid-cols-3 md:grid-cols-5 gap-4 max-w-3xl mx-auto text-center">
          {[
            ['15+', 'AI Agents'],
            ['50+', 'Languages'],
            ['∞', 'Cloud Resources'],
            ['3', '3D Canvas'],
            ['24/7', 'Always Online'],
          ].map(([num, label]) => (
            <div key={label} className="p-4 rounded-2xl glass-panel">
              <div className="text-2xl font-bold text-virgo-200">{num}</div>
              <div className="text-xs text-gray-500 mt-1 uppercase tracking-wider">{label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* Features */}
      <section id="features" className="max-w-6xl mx-auto px-6 py-20">
        <h2 className="text-3xl md:text-4xl font-bold text-center mb-4">
          <span className="accent-gradient-text">Everything You Need</span>
        </h2>
        <p className="text-center text-gray-500 max-w-xl mx-auto mb-12">
          One integrated environment. No more switching between a dozen tools.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {features.map((f, i) => (
            <div
              key={f.title}
              className="group glass-panel-hover p-6"
              style={{ animationDelay: `${i * 0.05}s` }}
            >
              <div
                className="h-12 w-12 rounded-xl flex items-center justify-center mb-4 transition-transform group-hover:scale-110"
                style={{ background: `${f.color}1a`, border: `1px solid ${f.color}33` }}
              >
                <f.icon size={22} style={{ color: f.color }} />
              </div>
              <h3 className="font-semibold text-lg text-white mb-2">{f.title}</h3>
              <p className="text-sm text-gray-500 leading-relaxed">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="max-w-6xl mx-auto px-6 py-20 text-center">
        <div className="glass-panel-hover p-12 md:p-16 rounded-3xl">
          <h2 className="text-3xl md:text-5xl font-bold mb-4">
            <span className="accent-gradient-text">Ready to Build?</span>
          </h2>
          <p className="text-gray-400 mb-8 max-w-xl mx-auto">
            Launch your cloud workspace and start building with AI. No setup, no install — just open your browser.
          </p>
          <Link
            href="/workspace"
            className="inline-flex items-center gap-2 px-8 py-4 rounded-2xl bg-gradient-to-r from-virgo-600 to-terminal-purple text-white font-semibold text-lg hover:shadow-2xl hover:shadow-virgo-600/40 transition-all"
          >
            Open VirgoYT Cloud AI
            <ArrowRight size={20} />
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-white/5 py-8">
        <div className="max-w-6xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <span className="text-sm accent-gradient-text font-semibold">{APP_NAME}</span>
            <span className="text-xs text-gray-600">v1.0.0</span>
          </div>
          <div className="flex items-center gap-6 text-sm text-gray-500">
            <a href={REPO_URL} target="_blank" rel="noreferrer" className="hover:text-white transition-colors">GitHub</a>
            <a href={LIVE_URL} target="_blank" rel="noreferrer" className="hover:text-white transition-colors">Live Demo</a>
          </div>
          <div className="text-xs text-gray-600">
            © {new Date().getFullYear()} darkvirgoyt-beep
          </div>
        </div>
      </footer>
    </main>
  );
}
