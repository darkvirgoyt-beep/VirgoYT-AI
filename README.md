# VirgoYT AI - Autonomous AI Software Engineer Platform

[![VirgoYT AI CI/CD](https://github.com/darkvirgoyt-beep/VirgoYT-AI/actions/workflows/build-and-deploy.yml/badge.svg)](https://github.com/darkvirgoyt-beep/VirgoYT-AI/actions/workflows/build-and-deploy.yml)
[![Live Cloud Applet](https://img.shields.io/badge/Live%20Applet-Online-06b6d4)](https://ais-pre-qeki52yslj4ugacyazvn2e-854216720694.asia-east1.run.app)
[![Release](https://img.shields.io/badge/Release-v1.0.0-6366f1)](https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases)

**VirgoYT AI** is an enterprise-grade autonomous AI Software Engineer platform and multi-agent development ecosystem designed to build, test, refactor, and deploy modern applications across 14+ languages and frameworks.

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

## 🔗 Live URLs & Access

- **Official Web App (GitHub Pages Hosted)**: [https://darkvirgoyt-beep.github.io/VirgoYT-AI/](https://darkvirgoyt-beep.github.io/VirgoYT-AI/)
- **GitHub Repository**: [https://github.com/darkvirgoyt-beep/VirgoYT-AI](https://github.com/darkvirgoyt-beep/VirgoYT-AI)
- **APK Releases**: [https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases](https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases)
- **Developer Live Preview**: [https://ais-dev-qeki52yslj4ugacyazvn2e-854216720694.asia-east1.run.app](https://ais-dev-qeki52yslj4ugacyazvn2e-854216720694.asia-east1.run.app)

