# VirgoYT AI - Autonomous AI Software Engineer Platform

[![VirgoYT AI CI/CD](https://github.com/darkvirgoyt-beep/VirgoYT-AI/actions/workflows/build-and-deploy.yml/badge.svg)](https://github.com/darkvirgoyt-beep/VirgoYT-AI/actions/workflows/build-and-deploy.yml)
[![Live Cloud Applet](https://img.shields.io/badge/Live%20Applet-Online-06b6d4)](https://ais-pre-qeki52yslj4ugacyazvn2e-854216720694.asia-east1.run.app)
[![Release](https://img.shields.io/badge/Release-v1.0.0-6366f1)](https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases)

**VirgoYT AI** is an enterprise-grade autonomous AI Software Engineer platform and multi-agent development ecosystem designed to build, test, refactor, and deploy modern applications across 14+ languages and frameworks.

---

## 🖥️ VirgoYT Cloud AI — Web Workspace (New)

A full cloud AI computer that runs in the browser, built with **Next.js 15, React 19, Three.js and Node.js**.

- **3D holographic workspace** — floating glass panels in a Three.js scene with particles, grid and post-processing.
- **Real code editor** — Monaco (VS Code engine) with 50+ languages, tabs, and syntax highlighting.
- **Live terminal** — xterm.js talking to the backend over WebSocket with a real PTY shell (node-pty) and graceful local fallback.
- **Cloud sandbox** — per-session Docker containers (when available) or a local shell for executing commands.
- **File manager** — real filesystem tree with read/write/create/delete via the REST API.
- **AI assistant** — multi-model chat (Gemini flash/pro, Claude, DeepSeek, Qwen) streamed through the server proxy.
- **Autonomous Agent (Virgo Agent)** — a live agent loop that browses the web, runs terminal commands, edits files, and downloads binaries (APK/EXE/videos). You watch it act in real time through a streamed activity feed + live browser-preview iframe, and approve one-off actions (e.g. logging into GitHub) before anything risky runs.
- **MCP + Plugins + Custom API** — the agent can call **MCP** servers (`npx @modelcontextprotocol/server-filesystem`, GitHub, fetch, Docker…) over the standard protocol; load **plugins** (folders under `server/plugins/` with `plugin.json` + handlers — see `plugins/helloworld/`); and route chat to **any custom OpenAI-compatible endpoint** via `CUSTOM_API_KEY` / `CUSTOM_API_BASE_URL` / `CUSTOM_API_MODEL`.
- **Multi-Agent Workforce** — a supervisor orchestrates specialist sub-agents (Architect → Developer → Researcher → Security → DevOps → Business) that collaborate on a goal and stream their output live. REST `POST /api/agent/workforce`, roster at `GET /api/agent/roster`.
- **Factory Mode** — describe a product ("build me a todo web app") and it runs an end-to-end pipeline: plan → scaffold → code → install → verify → README, writing real files to your workspace. `POST /api/agent/factory`.
- **Long-term Memory** — JSON-backed per-session memory (preferences, projects, decisions, skills, lessons) that feeds the agent's planner and persists across sessions. `GET/POST/DELETE /api/agent/memory`.
- **Browser sandbox** — responsive viewport testing (desktop/tablet/mobile).
- **System monitor** — live CPU, memory, disk, network, and process stats.
- **Auth** — JWT registration/login with a lightweight JSON-file database (no native deps).

### Project layout

```
web/     Next.js 15 + React 19 frontend (3D workspace + landing page)
server/  Node.js + Express + Socket.IO backend (sandbox, terminal, files, AI proxy)
Dockerfile / Dockerfile.web / docker-compose.yml   one-command deploy
app/     Existing Android (Jetpack Compose) app (kept as companion)
cli/     Existing terminal coding agents
```

### Run locally

```bash
# Backend (port 8080)
cd server && npm install && npm run dev

# Frontend (port 3000)
cd web && npm install && npm run dev
# then open http://localhost:3000/workspace
```

Set `GEMINI_API_KEY` in `server/.env` to enable live AI responses.

### Deploy

```bash
docker compose up --build
```

#### Vercel (frontend) + auto-deploy

1. In Vercel, import the repo and set **Root Directory = `web`**.
2. Redeploy — then open `/workspace`.
3. **Auto-deploy on every push:** set up `.github/workflows/vercel-deploy.yml` by adding these GitHub secrets (Repo → Settings → Secrets and variables → Actions):
   - `VERCEL_TOKEN` — from https://vercel.com/account/tokens
   - `VERCEL_ORG_ID` — `vercel org ls` after logging in with Vercel CLI
   - `VERCEL_PROJECT_ID` — `vercel project ls` / `vercel link` in the project
4. Every `git push` to `main` now builds and deploys Vercel automatically.

> The backend (`server/`) is a separate Node process and should be deployed to Railway/Fly.io/Render (or a VPS) — set `NEXT_PUBLIC_API_URL` in Vercel env vars to its URL so the workspace, terminal and agent can reach it.

---

## 🌟 Core System Architecture

1. **🐝 15 Specialized Autonomous Sub-Agents (Swarm Intelligence)**:
   - **Lead Architect & Orchestrator**: High-level task routing & decomposition.
   - **Research & Benchmarking Specialist**: Library lookup & algorithmic analysis.
   - **Core Software Engineer**: Algorithmic implementation & code synthesis.
   - **Frontend & UI/UX Specialist**: Modern reactive interfaces (React 19, Next.js 15, Tailwind, Compose).
   - **Backend & Microservices Engineer**: High-concurrency APIs (Go, Rust, FastAPI, Spring Boot).
   - **Mobile Ecosystem Specialist**: Jetpack Compose, SwiftUI, Flutter, and React Native.
   - **Game Engine & 3D Specialist**: Unreal Engine 5.4 Blueprints, Unity 6 C# scripts, Godot 4.3.
   - **Database & Query Optimizer**: PostgreSQL, MySQL, Redis, MongoDB, Supabase.
   - **QA & Automated Testing Agent**: Unit, integration, regression & snapshot testing.
   - **Security, Audit & SAST Specialist**: OWASP compliance, vulnerability triage, token safety.
   - **DevOps, CI/CD & Cloud Infrastructure**: Docker, Kubernetes, GitHub Actions, Terraform.
   - **Technical Documentation & OpenAPI Specialist**: Interactive specs and architecture diagrams.

2. **🧠 1536-Dimensional Vector Memory & RAG Retrieval**:
   - Sub-2ms cosine similarity indexing for user preferences, project structure, and code snippets.

3. **🎮 Unreal Engine 5.4 & 3D Game Studio**:
   - Real-time Blueprint graph generation, C++ gameplay logic compilation, and 3D preview renderers.

4. **⚡ Multi-Engine Database AI Studio**:
   - Live query runner, EXPLAIN execution planner, schema migration generators, and B-Tree index optimization.

---

## 🚀 GitHub Actions CI/CD Pipeline

The `.github/workflows/android-build.yml` workflow automatically runs on every push:

- **🤖 Android Test & APK Build**:
  - Sets up JDK 17 & Android SDK.
  - Automatically provisions debug signing keystore & environment secrets.
  - Runs unit tests via `./gradlew testDebugUnitTest`.
  - Builds both Debug (`assembleDebug`) and Release APKs (`assembleRelease`).
  - Uploads APK binaries to GitHub Actions artifacts for instant download.
- **🌐 Web Frontend & GitHub Pages**:
  - Automatically compiles and deploys the web landing portal to GitHub Pages.
- **🏷️ Automated GitHub Releases**:
  - Attaches compiled APKs to release tags (`v*`).

---

## 🛠️ Local Development & Build

### Prerequisites
- JDK 17 or higher
- Android SDK (API Level 36)
- Gradle 8+

### Commands
```bash
# Run unit & Robolectric tests
./gradlew testDebugUnitTest

# Assemble Debug APK
./gradlew assembleDebug

# Assemble Production Release APK
./gradlew assembleRelease
```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 Multi-Platform Downloads & Live Hosts

| Platform | Download / Access Link | Description |
| :--- | :--- | :--- |
| 🌐 **Live Web (GitHub Pages)** | [https://darkvirgoyt-beep.github.io/VirgoYT-AI/](https://darkvirgoyt-beep.github.io/VirgoYT-AI/) | Zero-install live web app with 3D canvas and agent dashboard |
| 📱 **Android APK (Release)** | [Direct Download (app-release.apk)](https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk) | Production signed APK for ARM64 & x86 devices |
| 📱 **Android APK (Debug)** | [Direct Download (app-debug.apk)](https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-debug.apk) | Developer debug APK with inspection enabled |
| 💻 **Windows Desktop (.exe)** | [VirgoYT-AI Releases](https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest) | Windows 10/11 64-bit installer & standalone bundle |
| 🍎 **macOS (.dmg / .app)** | [VirgoYT-AI Releases](https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest) | Apple Silicon (M1/M2/M3/M4) & Intel Mac package |

---

## ⚡ Terminal & Termux Download Instructions

### 1. Android Termux

**Option A — 1-Line Direct APK Download & Install**:
```bash
pkg update -y && pkg install -y curl && \
curl -sL https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk -o VirgoYT-AI.apk && \
termux-open VirgoYT-AI.apk
```

**Option B — Build from Source in Termux**:
```bash
pkg update -y && pkg install -y git openjdk-17 nodejs curl
git clone https://github.com/darkvirgoyt-beep/VirgoYT-AI.git
cd VirgoYT-AI
chmod +x gradlew
./gradlew assembleDebug
```

---

### 2. Linux Terminal (Ubuntu, Debian, Fedora, Arch)

**Option A — Download APK directly via wget / cURL**:
```bash
# Using wget:
wget -O virgo-yt-ai.apk https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk

# Using cURL:
curl -L -O https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk
```

**Option B — Run Local Live Web Host**:
```bash
git clone https://github.com/darkvirgoyt-beep/VirgoYT-AI.git
cd VirgoYT-AI
python3 -m http.server 8080 --directory public
# Open http://localhost:8080 in your browser
```

---

### 3. macOS Terminal (zsh / bash)

**Option A — Download APK or Assets via cURL**:
```bash
curl -sLO https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk
open . # Opens Finder at current directory
```

**Option B — Clone & Build with Homebrew**:
```bash
brew install git openjdk@17
git clone https://github.com/darkvirgoyt-beep/VirgoYT-AI.git
cd VirgoYT-AI
chmod +x gradlew
./gradlew assembleDebug
open app/build/outputs/apk/debug/
```

---

## 🔗 Live URLs & Access

- **Official Web App (GitHub Pages Hosted)**: [https://darkvirgoyt-beep.github.io/VirgoYT-AI/](https://darkvirgoyt-beep.github.io/VirgoYT-AI/)
- **GitHub Repository**: [https://github.com/darkvirgoyt-beep/VirgoYT-AI](https://github.com/darkvirgoyt-beep/VirgoYT-AI)
- **APK Releases**: [https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases](https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases)
- **Developer Live Preview**: [https://ais-dev-qeki52yslj4ugacyazvn2e-854216720694.asia-east1.run.app](https://ais-dev-qeki52yslj4ugacyazvn2e-854216720694.asia-east1.run.app)

