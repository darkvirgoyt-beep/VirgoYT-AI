package com.example.virgoyt.data.vfs

import com.example.virgoyt.data.model.VirtualFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

class VirtualFileSystem {

    private val files = ConcurrentHashMap<String, VirtualFile>()
    private val _fileListState = MutableStateFlow<List<VirtualFile>>(emptyList())
    val fileListState: StateFlow<List<VirtualFile>> = _fileListState.asStateFlow()

    init {
        resetToDefaults()
    }

    fun resetToDefaults() {
        files.clear()
        addDir("/workspace")
        addDir("/workspace/src")
        addDir("/workspace/public")
        addDir("/workspace/users")
        addDir("/workspace/users/virgoyt")
        addDir("/workspace/scripts")
        addDir("/workspace/database")

        addFile(
            "/workspace/package.json",
            "package.json",
            """{
  "name": "virgoyt-cloud-application",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "dev": "next dev --turbopack",
    "build": "next build",
    "start": "next start",
    "test": "vitest"
  },
  "dependencies": {
    "next": "^15.1.0",
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "lucide-react": "^0.468.0",
    "three": "^0.170.0",
    "framer-motion": "^11.13.0",
    "tailwindcss": "^3.4.1"
  }
}"""
        )

        addFile(
            "/workspace/src/App.tsx",
            "App.tsx",
            """import React, { useState } from 'react';

export default function VirgoApp() {
  const [status, setStatus] = useState('Online');
  return (
    <div className="min-h-screen bg-slate-950 text-white flex flex-col items-center justify-center p-6">
      <div className="p-8 rounded-2xl bg-slate-900 border border-cyan-500/30 shadow-2xl shadow-cyan-500/10 text-center max-w-md">
        <h1 className="text-3xl font-bold bg-gradient-to-r from-cyan-400 via-indigo-300 to-purple-400 bg-clip-text text-transparent">
          VirgoYT Cloud AI
        </h1>
        <p className="mt-2 text-slate-400 text-sm">
          Autonomous Cloud Developer Swarm & Multi-Engine Sandbox
        </p>
        <div className="mt-6 inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-cyan-950/80 border border-cyan-500/40 text-cyan-300 text-xs font-mono">
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
          Cluster Status: {status}
        </div>
      </div>
    </div>
  );
}"""
        )

        addFile(
            "/workspace/src/server.py",
            "server.py",
            """from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="VirgoYT Cloud Backend")

class DeployRequest(BaseModel):
    app_name: str
    replicas: int = 3

@app.get("/")
def health_check():
    return {"status": "healthy", "service": "virgoyt-cloud-ai", "region": "asia-east1"}

@app.post("/api/v1/deploy")
def deploy(req: DeployRequest):
    return {"deployed": True, "app": req.app_name, "replicas": req.replicas}
"""
        )

        addFile(
            "/workspace/database/schema.sql",
            "schema.sql",
            """CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE workspaces (
    id VARCHAR(36) PRIMARY KEY,
    owner_id VARCHAR(36) REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    cpu_limit_cores INT DEFAULT 8,
    memory_limit_mb INT DEFAULT 16384
);
"""
        )

        addFile(
            "/workspace/README.md",
            "README.md",
            """# VirgoYT Cloud AI Development Workspace
This sandbox environment features:
- Virtual File System (VFS) with real-time diff tracking
- Multi-Model AI Routing (Gemini 2.5 Pro, Claude 3.7, DeepSeek R1, Llama 3.3)
- Live Three.js & Unreal 3D Game Studio
- Next.js Web Application Live Preview Sandbox
- Full Autonomous Agent Swarm Pipeline (Architect -> Coder -> Reviewer -> DevOps)
"""
        )

        refreshList()
    }

    fun addDir(path: String, owner: String = "root") {
        val cleanPath = normalizePath(path)
        val name = cleanPath.substringAfterLast("/").ifEmpty { "/" }
        files[cleanPath] = VirtualFile(
            path = cleanPath,
            name = name,
            isDirectory = true,
            owner = owner,
            permissions = "drwxr-xr-x"
        )
    }

    fun addFile(path: String, name: String, content: String, owner: String = "root") {
        val cleanPath = normalizePath(path)
        files[cleanPath] = VirtualFile(
            path = cleanPath,
            name = name,
            isDirectory = false,
            content = content,
            owner = owner,
            permissions = "-rw-r--r--"
        )
        refreshList()
    }

    fun readFile(path: String): String? {
        val cleanPath = normalizePath(path)
        return files[cleanPath]?.content
    }

    fun writeFile(path: String, content: String, owner: String = "root"): Boolean {
        val cleanPath = normalizePath(path)
        val name = cleanPath.substringAfterLast("/")
        val existing = files[cleanPath]
        files[cleanPath] = VirtualFile(
            path = cleanPath,
            name = name,
            isDirectory = false,
            content = content,
            lastModified = System.currentTimeMillis(),
            owner = existing?.owner ?: owner,
            permissions = existing?.permissions ?: "-rw-r--r--"
        )
        refreshList()
        return true
    }

    fun deleteFile(path: String): Boolean {
        val cleanPath = normalizePath(path)
        val removed = files.remove(cleanPath) != null
        if (removed) {
            // Also remove child files if it's a directory
            files.keys.filter { it.startsWith("$cleanPath/") }.forEach { files.remove(it) }
            refreshList()
        }
        return removed
    }

    fun createDirectory(path: String, owner: String = "root"): Boolean {
        addDir(path, owner)
        refreshList()
        return true
    }

    fun renameFile(oldPath: String, newPath: String): Boolean {
        val cleanOld = normalizePath(oldPath)
        val cleanNew = normalizePath(newPath)
        val file = files.remove(cleanOld) ?: return false
        val newName = cleanNew.substringAfterLast("/")
        files[cleanNew] = file.copy(path = cleanNew, name = newName)
        refreshList()
        return true
    }

    fun copyFile(sourcePath: String, destPath: String, owner: String = "root"): Boolean {
        val cleanSrc = normalizePath(sourcePath)
        val cleanDst = normalizePath(destPath)
        val srcFile = files[cleanSrc] ?: return false
        val newName = cleanDst.substringAfterLast("/")
        files[cleanDst] = srcFile.copy(path = cleanDst, name = newName, owner = owner)
        refreshList()
        return true
    }

    fun moveFile(sourcePath: String, destPath: String): Boolean {
        if (copyFile(sourcePath, destPath)) {
            deleteFile(sourcePath)
            return true
        }
        return false
    }

    fun getFile(path: String): VirtualFile? {
        return files[normalizePath(path)]
    }

    fun listFilesInDir(dirPath: String): List<VirtualFile> {
        val cleanDir = normalizePath(dirPath).removeSuffix("/")
        return files.values.filter { file ->
            val parent = file.path.substringBeforeLast("/")
            parent == cleanDir && file.path != cleanDir
        }.sortedWith(compareBy<VirtualFile> { !it.isDirectory }.thenBy { it.name })
    }

    fun getAllFiles(): List<VirtualFile> {
        return files.values.sortedWith(compareBy<VirtualFile> { !it.isDirectory }.thenBy { it.path })
    }

    fun getUserFiles(username: String): List<VirtualFile> {
        val prefix = "/workspace/users/$username"
        return files.values.filter { it.path.startsWith(prefix) }
            .sortedWith(compareBy<VirtualFile> { !it.isDirectory }.thenBy { it.path })
    }

    private fun normalizePath(path: String): String {
        var p = path.trim()
        if (!p.startsWith("/")) p = "/$p"
        while (p.contains("//")) p = p.replace("//", "/")
        if (p.length > 1 && p.endsWith("/")) p = p.removeSuffix("/")
        return p
    }

    private fun refreshList() {
        _fileListState.value = files.values.sortedWith(
            compareBy<VirtualFile> { !it.isDirectory }.thenBy { it.path }
        )
    }

    fun getBundledWebPreviewHtml(): String {
        val appCode = readFile("/workspace/src/App.tsx") ?: "<h1>VirgoYT Cloud Sandbox</h1>"
        return """<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>VirgoYT Cloud AI Live Preview</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-950 text-slate-100 flex flex-col items-center justify-center min-h-screen p-4">
    <div class="max-w-lg w-full bg-slate-900 border border-cyan-500/30 rounded-2xl p-6 shadow-2xl text-center">
        <div class="w-12 h-12 rounded-xl bg-cyan-500/20 border border-cyan-400/40 mx-auto flex items-center justify-center text-2xl">
            ⚡
        </div>
        <h2 class="text-2xl font-bold text-cyan-300 mt-4">VirgoYT Cloud Application</h2>
        <p class="text-slate-400 text-sm mt-1">Live Sandbox Rendering Environment</p>
        <div class="mt-4 p-4 rounded-xl bg-slate-950 border border-slate-800 text-left font-mono text-xs overflow-x-auto text-emerald-400">
            <pre>${appCode.replace("<", "&lt;").replace(">", "&gt;")}</pre>
        </div>
    </div>
</body>
</html>"""
    }
}
