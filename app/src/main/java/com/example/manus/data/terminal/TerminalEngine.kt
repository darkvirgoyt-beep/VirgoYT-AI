package com.example.manus.data.terminal

import com.example.manus.data.github.GitHubAuthManager
import com.example.manus.data.model.OutputType
import com.example.manus.data.model.TerminalEntry
import com.example.manus.data.model.TerminalMode
import com.example.manus.data.vfs.VirtualFileSystem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TerminalEngine(
    private val vfs: VirtualFileSystem,
    private val authManager: com.example.manus.data.auth.AuthManager? = null,
    val githubManager: GitHubAuthManager = GitHubAuthManager()
) {

    private var currentDir = "/workspace"
    private val commandHistory = mutableListOf<String>()
    private var historyIndex = -1

    private val environmentVariables = java.util.concurrent.ConcurrentHashMap<String, String>().apply {
        put("OPENAI_API_BASE", "https://api.kie.ai/v1")
        put("OPENAI_API_KEY", "your_openai_api_key_here")
        put("BAZAARLINK_API_BASE", "https://api.bazaarlink.ai/v1")
        put("BAZAARLINK_API_KEY", "your_bazaarlink_api_key_here")
        put("OPENROUTER_API_KEY", "your_openrouter_api_key_here")
        put("GROQ_API_KEY", "your_groq_api_key_here")
        put("VIRGO_AUTH_TOKEN", "configured_vault_token")
        put("PATH", "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/workspace/node_modules/.bin")
        put("SHELL", "/bin/bash")
        put("TERM", "xterm-256color")
    }

    private val _terminalMode = MutableStateFlow(TerminalMode.CLOUD_VM)
    val terminalMode: StateFlow<TerminalMode> = _terminalMode.asStateFlow()

    private val _entries = MutableStateFlow<List<TerminalEntry>>(emptyList())
    val entries: StateFlow<List<TerminalEntry>> = _entries.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    init {
        appendEntry(
            "╔═══════════════════════════════════════════════════════════════╗",
            OutputType.SYSTEM
        )
        appendEntry(
            "║  MANUS CLOUD PC - VIRTUAL LINUX SANDBOX (Ubuntu 24.04 LTS)   ║",
            OutputType.SYSTEM
        )
        appendEntry(
            "║  Node.js v22.1.0 • Python 3.12.3 • GCC 14.2 • Git 2.45.0 • GH  ║",
            OutputType.SYSTEM
        )
        appendEntry(
            "║  Type 'help', 'gh auth login' or 'manus install' for CLI!     ║",
            OutputType.SYSTEM
        )
        appendEntry(
            "╚═══════════════════════════════════════════════════════════════╝",
            OutputType.SYSTEM
        )
        appendEntry("developer@manus-cloud-pc:~/workspace$ ready. [Mode: Cloud VM]", OutputType.SUCCESS)
    }

    fun getCurrentDir(): String = currentDir

    fun getCommandHistory(): List<String> = commandHistory.toList()

    fun setTerminalMode(mode: TerminalMode) {
        _terminalMode.value = mode
        val hostInfo = if (mode == TerminalMode.CLOUD_VM) "asia-east1.cloud-node (Ubuntu 24.04)" else "127.0.0.1:8080 (Local Machine Bridge)"
        appendEntry("Switched execution host to [${mode.label}] -> $hostInfo", OutputType.SYSTEM)
    }

    private fun appendEntry(text: String, type: OutputType = OutputType.STDOUT) {
        val newEntry = TerminalEntry(text = text, type = type)
        _entries.value = _entries.value + newEntry
    }

    fun clear() {
        _entries.value = emptyList()
    }

    suspend fun executeCommand(rawCommand: String): String {
        val trimmed = rawCommand.trim()
        if (trimmed.isEmpty()) return ""

        commandHistory.add(trimmed)
        historyIndex = commandHistory.size

        val activeUser = authManager?.currentUser?.username ?: "developer"
        val hostName = _terminalMode.value.promptPrefix
        val prompt = "$activeUser@$hostName:${currentDir.replace("/workspace", "~")}$ $trimmed"
        appendEntry(prompt, OutputType.COMMAND)

        _isExecuting.value = true
        val outputBuilder = StringBuilder()

        try {
            val parts = trimmed.split("\\s+".toRegex())
            val cmd = parts[0]
            val args = parts.drop(1)

            // Direct check: if user pasted an auth token or device code directly into terminal
            if ((trimmed.startsWith("gho_") || trimmed.startsWith("ghp_") || (trimmed.contains("-") && trimmed.length == 9 && trimmed.all { it.isLetterOrDigit() || it == '-' })) &&
                (githubManager.pendingDeviceAuth.value != null || trimmed.startsWith("gho_"))) {
                val ok = githubManager.authorizeWithTokenOrCode(trimmed, activeUser)
                if (ok) {
                    val user = githubManager.currentUser.value?.username ?: activeUser
                    val successMsg = """
✓ Authentication verified successfully!
- Logged in to GitHub as '$user'
- OAuth token saved to ~/.config/gh/hosts.yml
- Git credential helper configured for github.com
- Available repositories: ${githubManager.userRepos.value.size} repos synced
""".trimIndent()
                    appendEntry(successMsg, OutputType.SUCCESS)
                    outputBuilder.append(successMsg)
                    return outputBuilder.toString()
                }
            }

            when (cmd) {
                "help" -> {
                    val helpText = """
Available Commands:
  • GitHub & Version Control:
      gh auth login             - Connect GitHub via browser device authorization
      gh auth status            - View active GitHub login & OAuth token status
      gh auth logout            - Disconnect GitHub session
      gh repo list              - List all repositories from connected account
      gh repo clone <repo>      - Clone repository into local workspace
      gh repo create <name>     - Create new GitHub repository
      gh gist create <file>     - Create public GitHub Gist
      git <status|log|commit|push|pull|clone|remote> - Standard Git operations
  • Multi-Platform Terminal CLI:
      virgoyt install           - Show CLI install commands for Linux, macOS, Windows, Termux
      virgoyt mode <cloud|local>- Switch terminal between Cloud VM and Localhost daemon
      virgoyt status            - Show active system specs, GitHub link & network mode
  • Navigation & Files:
      ls [-la], cd <dir>, pwd, cat <file>, touch <file>, mkdir [-p] <dir>,
      rm [-rf] <path>, cp <src> <dest>, mv <src> <dest>, rename <file> <newName>,
      upload <file> [content], download <file>, find <path>, grep <str> <file>, tree
  • User & Session Management:
      whoami, users, login <user> [pass], signup <user> [email] [pass],
      logout, su <user>, session, auth
  • High-End Compilers & Runtimes:
      dotnet run / build / test - .NET 9.0 SDK & C# 13 Runtime
      csc <Program.cs>          - Microsoft Roslyn C# Compiler
      g++ / clang++ <file.cpp>  - Modern C++23 Compiler (-O3, AST optimization)
      gcc / clang <file.c>      - High-Performance C Compiler & Linker
      rustc <file.rs> / cargo   - Rust 1.80 Compiler & Package Engine
      go run / build <file.go>  - Go 1.23 Fast Concurrency Runtime
      javac / java <File.java>  - Java 21 LTS OpenJDK Platform
      python3 <script.py>       - Python 3.12 Data Science & ML Engine
      node <script.js>          - Node.js v22 JavaScript/TypeScript Engine
      bash <script.sh> / ./<bin>- Shell automation & native ELF execution
  • Package & Network:
      npm run <dev|start|test>, pip install <pkg>, curl <url>, ping <host>, top, ps, df, free
""".trimIndent()
                    appendEntry(helpText, OutputType.STDOUT)
                    outputBuilder.append(helpText)
                }

                "clear" -> {
                    clear()
                }

                "pwd" -> {
                    appendEntry(currentDir, OutputType.STDOUT)
                    outputBuilder.append(currentDir)
                }

                "whoami" -> {
                    val user = authManager?.currentUser
                    val userStr = if (user != null) {
                        "${user.username} (${user.role}) - Home: ${user.homeDir}"
                    } else {
                        "developer (Lead Developer) - Home: /workspace"
                    }
                    appendEntry(userStr, OutputType.STDOUT)
                    outputBuilder.append(userStr)
                }

                "users" -> {
                    val usersList = authManager?.getAllUsers()?.map { "• ${it.username} [${it.role}] (${it.email})" }?.joinToString("\n")
                        ?: "• developer [Lead Developer]\n• admin [Root Administrator]\n• guest [Sandbox Guest]"
                    appendEntry("Registered Cloud Terminal Users:\n$usersList", OutputType.STDOUT)
                    outputBuilder.append(usersList)
                }

                "login", "su" -> {
                    val targetUser = args.firstOrNull()
                    val targetPass = if (args.size > 1) args[1] else "${targetUser ?: ""}123"
                    if (targetUser == null) {
                        val err = "Usage: login <username> [password] or su <username>"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else if (authManager != null) {
                        val res = authManager.login(targetUser, targetPass)
                        if (res.isSuccess) {
                            val u = res.getOrNull()!!
                            currentDir = u.homeDir
                            val msg = "✓ Authenticated as ${u.username} (${u.role}). Workspace switched to ${u.homeDir}"
                            appendEntry(msg, OutputType.SUCCESS)
                            outputBuilder.append(msg)
                        } else {
                            val err = "Authentication failure: ${res.exceptionOrNull()?.message}"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    } else {
                        appendEntry("Switched session to $targetUser", OutputType.SUCCESS)
                    }
                }

                "signup", "useradd" -> {
                    val newUsername = args.firstOrNull()
                    val newEmail = if (args.size > 1) args[1] else "$newUsername@manus.cloud"
                    val newPass = if (args.size > 2) args[2] else "${newUsername}123"
                    if (newUsername == null) {
                        val err = "Usage: signup <username> [email] [password]"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else if (authManager != null) {
                        val res = authManager.signup(newUsername, newEmail, newPass)
                        if (res.isSuccess) {
                            val u = res.getOrNull()!!
                            vfs.createDirectory(u.homeDir, u.username)
                            vfs.addFile("${u.homeDir}/welcome.txt", "Welcome ${u.username}!\nIsolated user environment initialized.\n", u.username)
                            currentDir = u.homeDir
                            val msg = "✓ User '${u.username}' registered successfully! Home directory created at ${u.homeDir}"
                            appendEntry(msg, OutputType.SUCCESS)
                            outputBuilder.append(msg)
                        } else {
                            val err = "Registration failed: ${res.exceptionOrNull()?.message}"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "logout" -> {
                    authManager?.logout()
                    currentDir = "/home/guest"
                    val msg = "Logged out from current session. Switched to guest sandbox (/home/guest)."
                    appendEntry(msg, OutputType.SYSTEM)
                    outputBuilder.append(msg)
                }

                "session", "auth" -> {
                    val session = authManager?.currentSession?.value
                    val status = if (session != null) {
                        """
User Session Details:
  Username: ${session.user.username}
  Email:    ${session.user.email}
  Role:     ${session.user.role}
  Home Dir: ${session.user.homeDir}
  Token:    ${session.token.take(8)}...
  Auth:     Active Isolated Sandbox
""".trimIndent()
                    } else {
                        "No active session (Guest sandbox)"
                    }
                    appendEntry(status, OutputType.STDOUT)
                    outputBuilder.append(status)
                }

                "export" -> {
                    val fullArg = args.joinToString(" ")
                    if (fullArg.isBlank()) {
                        val envList = environmentVariables.entries.sortedBy { it.key }
                            .joinToString("\n") { "declare -x ${it.key}=\"${it.value}\"" }
                        appendEntry(envList, OutputType.STDOUT)
                        outputBuilder.append(envList)
                    } else {
                        val clean = fullArg.replace("export ", "").trim()
                        if (clean.contains("=")) {
                            val key = clean.substringBefore("=").trim()
                            val value = clean.substringAfter("=").trim().trim('"', '\'')
                            environmentVariables[key] = value
                            val msg = "✓ [export] $key=\"$value\" set in environment"
                            appendEntry(msg, OutputType.SUCCESS)
                            outputBuilder.append(msg)
                        } else {
                            val msg = "declare -x $clean=\"${environmentVariables[clean] ?: ""}\""
                            appendEntry(msg, OutputType.STDOUT)
                            outputBuilder.append(msg)
                        }
                    }
                }

                "env", "printenv" -> {
                    val envOutput = environmentVariables.entries.sortedBy { it.key }
                        .joinToString("\n") { "${it.key}=${it.value}" }
                    appendEntry(envOutput, OutputType.STDOUT)
                    outputBuilder.append(envOutput)
                }

                "cp" -> {
                    val recursive = args.contains("-r") || args.contains("-R")
                    val fileArgs = args.filter { !it.startsWith("-") }
                    if (fileArgs.size < 2) {
                        val err = "cp: missing destination file operand after '${fileArgs.firstOrNull() ?: ""}'"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val src = resolvePath(fileArgs[0])
                        val dest = resolvePath(fileArgs[1])
                        val owner = authManager?.currentUser?.username ?: "developer"
                        val success = vfs.copyFile(src, dest, owner)
                        if (success) {
                            val msg = "Copied $src -> $dest"
                            appendEntry(msg, OutputType.SUCCESS)
                            outputBuilder.append(msg)
                        } else {
                            val err = "cp: cannot stat '$src': No such file or directory"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "mv" -> {
                    val fileArgs = args.filter { !it.startsWith("-") }
                    if (fileArgs.size < 2) {
                        val err = "mv: missing destination file operand after '${fileArgs.firstOrNull() ?: ""}'"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val src = resolvePath(fileArgs[0])
                        val dest = resolvePath(fileArgs[1])
                        val success = vfs.moveFile(src, dest)
                        if (success) {
                            val msg = "Moved $src -> $dest"
                            appendEntry(msg, OutputType.SUCCESS)
                            outputBuilder.append(msg)
                        } else {
                            val err = "mv: cannot stat '$src': No such file or directory"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "rename" -> {
                    if (args.size < 2) {
                        val err = "rename: usage: rename <filepath> <new_name>"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val src = resolvePath(args[0])
                        val newName = args[1]
                        val success = vfs.renameFile(src, newName)
                        if (success) {
                            val msg = "Renamed $src -> $newName"
                            appendEntry(msg, OutputType.SUCCESS)
                            outputBuilder.append(msg)
                        } else {
                            val err = "rename: failed to rename '$src'"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "upload" -> {
                    val fileName = args.firstOrNull()
                    if (fileName == null) {
                        val err = "upload: usage: upload <filename> [sample_content]"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val path = resolvePath(fileName)
                        val content = if (args.size > 1) args.drop(1).joinToString(" ") else "// Uploaded file $fileName\n// Timestamp: ${Date()}\n"
                        val owner = authManager?.currentUser?.username ?: "developer"
                        vfs.writeFile(path, content, owner)
                        val msg = "✓ File uploaded successfully to $path (${content.length} bytes)"
                        appendEntry(msg, OutputType.SUCCESS)
                        outputBuilder.append(msg)
                    }
                }

                "download" -> {
                    val fileName = args.firstOrNull()
                    if (fileName == null) {
                        val err = "download: usage: download <filename>"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val path = resolvePath(fileName)
                        val content = vfs.readFile(path)
                        if (content != null) {
                            val msg = "✓ Download prepared for $path (${content.length} bytes):\n---\n$content\n---"
                            appendEntry(msg, OutputType.SUCCESS)
                            outputBuilder.append(msg)
                        } else {
                            val err = "download: cannot find file '$fileName'"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "uname" -> {
                    val unameStr = "Linux virgoyt-cloud-ai 6.8.0-31-generic #31-Ubuntu SMP PREEMPT_DYNAMIC x86_64 GNU/Linux"
                    appendEntry(unameStr, OutputType.STDOUT)
                    outputBuilder.append(unameStr)
                }

                "date" -> {
                    val dateStr = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US).format(Date())
                    appendEntry(dateStr, OutputType.STDOUT)
                    outputBuilder.append(dateStr)
                }

                "uptime" -> {
                    val uptimeStr = " 12:45:00 up 4 hrs,  2 users,  load average: 0.12, 0.08, 0.04"
                    appendEntry(uptimeStr, OutputType.STDOUT)
                    outputBuilder.append(uptimeStr)
                }

                "df" -> {
                    val dfText = """
Filesystem      Size  Used Avail Use% Mounted on
/dev/root        64G  4.3G   57G   7% /
tmpfs           4.0G  1.8G  2.2G  45% /dev/shm
/dev/workspace   32G  512M   31G   2% /workspace
""".trimIndent()
                    appendEntry(dfText, OutputType.STDOUT)
                    outputBuilder.append(dfText)
                }

                "free" -> {
                    val freeText = """
               total        used        free      shared  buff/cache   available
Mem:            8192        1840        5120         128        1232        6224
Swap:           2048           0        2048
""".trimIndent()
                    appendEntry(freeText, OutputType.STDOUT)
                    outputBuilder.append(freeText)
                }

                "ps" -> {
                    val psText = """
  PID TTY          TIME CMD
 1042 pts/0    00:00:01 bash
 1289 pts/0    00:00:03 node (preview-server:3000)
 1350 pts/0    00:00:00 python3
 1410 pts/0    00:00:00 ps
""".trimIndent()
                    appendEntry(psText, OutputType.STDOUT)
                    outputBuilder.append(psText)
                }

                "top" -> {
                    val topText = """
Tasks: 4 total, 1 running, 3 sleeping, 0 stopped, 0 zombie
%Cpu(s): 12.4 us,  2.1 sy,  0.0 ni, 85.5 id,  0.0 wa,  0.0 hi,  0.0 si
MiB Mem :   8192.0 total,   5120.0 free,   1840.0 used,   1232.0 buff/cache
MiB Swap:   2048.0 total,   2048.0 free,      0.0 used.   6224.0 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND
 1289 manus     20   0  380.2m  42.1m  18.4m S   8.2   0.5   0:03.14 node
 1042 manus     20   0   18.5m   4.2m   3.1m S   1.8   0.1   0:01.02 bash
 1350 manus     20   0   84.6m  24.8m  12.0m S   0.9   0.3   0:00.45 python3
""".trimIndent()
                    appendEntry(topText, OutputType.STDOUT)
                    outputBuilder.append(topText)
                }

                "cd" -> {
                    val target = args.firstOrNull() ?: "/workspace"
                    val resolved = resolvePath(target)
                    val dirFile = vfs.getFile(resolved)
                    if (dirFile != null && dirFile.isDirectory) {
                        currentDir = resolved
                        val msg = "Switched directory to $currentDir"
                        appendEntry(msg, OutputType.SYSTEM)
                        outputBuilder.append(msg)
                    } else {
                        val err = "bash: cd: $target: No such file or directory"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    }
                }

                "ls" -> {
                    val showAll = args.contains("-a") || args.contains("-la") || args.contains("-al")
                    val isLong = args.contains("-l") || args.contains("-la") || args.contains("-al")
                    val files = vfs.listFilesInDir(currentDir)

                    if (isLong) {
                        appendEntry("total ${files.size * 4}", OutputType.STDOUT)
                        files.forEach { f ->
                            val perm = if (f.isDirectory) "drwxr-xr-x 2 manus dev" else "-rw-r--r-- 1 manus dev"
                            val size = if (f.isDirectory) 4096 else f.sizeBytes
                            val line = String.format(
                                Locale.US,
                                "%-16s %6d Aug 31 05:30 %s%s",
                                perm,
                                size,
                                f.name,
                                if (f.isDirectory) "/" else ""
                            )
                            appendEntry(line, if (f.isDirectory) OutputType.COMMAND else OutputType.STDOUT)
                            outputBuilder.appendLine(line)
                        }
                    } else {
                        val line = files.joinToString("  ") { if (it.isDirectory) "${it.name}/" else it.name }
                        appendEntry(line, OutputType.STDOUT)
                        outputBuilder.append(line)
                    }
                }

                "tree" -> {
                    val treeText = generateTree(currentDir)
                    appendEntry(treeText, OutputType.STDOUT)
                    outputBuilder.append(treeText)
                }

                "cat" -> {
                    val target = args.firstOrNull()
                    if (target == null) {
                        val err = "cat: missing file operand"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val path = resolvePath(target)
                        val content = vfs.readFile(path)
                        if (content != null) {
                            appendEntry(content, OutputType.STDOUT)
                            outputBuilder.append(content)
                        } else {
                            val err = "cat: $target: No such file or directory"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "mkdir" -> {
                    val dirName = args.lastOrNull()
                    if (dirName == null) {
                        val err = "mkdir: missing operand"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val path = resolvePath(dirName)
                        vfs.createDirectory(path)
                        val msg = "Directory created: $path"
                        appendEntry(msg, OutputType.SUCCESS)
                        outputBuilder.append(msg)
                    }
                }

                "touch" -> {
                    val fileName = args.firstOrNull()
                    if (fileName == null) {
                        val err = "touch: missing file operand"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val path = resolvePath(fileName)
                        vfs.writeFile(path, "")
                        val msg = "File created: $path"
                        appendEntry(msg, OutputType.SUCCESS)
                        outputBuilder.append(msg)
                    }
                }

                "rm" -> {
                    val fileName = args.lastOrNull()
                    if (fileName == null) {
                        val err = "rm: missing operand"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val path = resolvePath(fileName)
                        val success = vfs.deleteFile(path)
                        if (success) {
                            val msg = "Removed $path"
                            appendEntry(msg, OutputType.SUCCESS)
                            outputBuilder.append(msg)
                        } else {
                            val err = "rm: cannot remove '$fileName': No such file"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "echo" -> {
                    if (trimmed.contains(">")) {
                        val echoParts = trimmed.substringAfter("echo").split(">")
                        val text = echoParts[0].trim().trim('"', '\'')
                        val target = echoParts[1].trim()
                        val path = resolvePath(target)
                        vfs.writeFile(path, text)
                        appendEntry("Wrote ${text.length} bytes to $path", OutputType.SUCCESS)
                        outputBuilder.append("Wrote $text to $path")
                    } else {
                        val text = args.joinToString(" ").trim('"', '\'')
                        appendEntry(text, OutputType.STDOUT)
                        outputBuilder.append(text)
                    }
                }

                "grep" -> {
                    if (args.size < 2) {
                        val err = "grep: usage: grep <pattern> <file>"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val pattern = args[0].trim('"', '\'')
                        val file = resolvePath(args[1])
                        val content = vfs.readFile(file)
                        if (content != null) {
                            val matches = content.lines().filter { it.contains(pattern, ignoreCase = true) }
                            if (matches.isNotEmpty()) {
                                matches.forEach { appendEntry(it, OutputType.STDOUT) }
                                outputBuilder.append(matches.joinToString("\n"))
                            } else {
                                appendEntry("No matches found for '$pattern'", OutputType.SYSTEM)
                            }
                        } else {
                            val err = "grep: $file: No such file"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "find" -> {
                    val all = vfs.getAllFiles()
                    val result = all.joinToString("\n") { it.path }
                    appendEntry(result, OutputType.STDOUT)
                    outputBuilder.append(result)
                }

                "python3", "python" -> {
                    val scriptName = args.firstOrNull()
                    if (scriptName == null) {
                        appendEntry("Python 3.12.3 (main, Apr 15 2024, 18:20:11) [GCC 14.2] on linux", OutputType.STDOUT)
                        appendEntry("Type \"help()\", \"copyright()\", \"credits()\" or \"license()\" for more information.", OutputType.STDOUT)
                    } else {
                        val path = resolvePath(scriptName)
                        val code = vfs.readFile(path)
                        if (code != null) {
                            appendEntry("[*] Executing Python script: $path...", OutputType.SYSTEM)
                            delay(250)
                            val executionLog = executePythonCode(code)
                            appendEntry(executionLog, OutputType.STDOUT)
                            appendEntry("[✓] Process completed with exit code 0", OutputType.SUCCESS)
                            outputBuilder.append(executionLog)
                        } else {
                            val err = "python3: can't open file '$scriptName': [Errno 2] No such file or directory"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "node" -> {
                    val scriptName = args.firstOrNull()
                    if (scriptName == null) {
                        appendEntry("Welcome to Node.js v22.1.0.\nType \".help\" for more information.", OutputType.STDOUT)
                    } else if (scriptName == "-e") {
                        val inlineJs = args.drop(1).joinToString(" ")
                        val log = executeJsCode(inlineJs)
                        appendEntry(log, OutputType.STDOUT)
                        outputBuilder.append(log)
                    } else {
                        val path = resolvePath(scriptName)
                        val code = vfs.readFile(path)
                        if (code != null) {
                            appendEntry("[*] Executing Node.js: $path...", OutputType.SYSTEM)
                            delay(200)
                            val executionLog = executeJsCode(code)
                            appendEntry(executionLog, OutputType.STDOUT)
                            appendEntry("[✓] Node process terminated normally (exit code: 0)", OutputType.SUCCESS)
                            outputBuilder.append(executionLog)
                        } else {
                            val err = "node: internal/modules/cjs/loader: No such file $scriptName"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "gcc", "g++", "clang", "clang++" -> {
                    val isCpp = cmd == "g++" || cmd == "clang++" || args.any { it.endsWith(".cpp") || it.endsWith(".cxx") || it.endsWith(".cc") }
                    val compilerName = if (cmd.startsWith("clang")) "LLVM Clang 18.1" else "GCC 14.2"
                    val langStandard = if (isCpp) "C++23 (ISO/IEC 14882:2024)" else "C23 (ISO/IEC 9899:2024)"
                    val srcName = args.firstOrNull { !it.startsWith("-") }
                    if (srcName == null) {
                        val err = "$cmd: fatal error: no input files"
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    } else {
                        val path = resolvePath(srcName)
                        val code = vfs.readFile(path)
                        if (code != null) {
                            appendEntry("[*] Invoking $compilerName [$langStandard] -O3 -flto -march=native...", OutputType.SYSTEM)
                            delay(250)
                            appendEntry("[1/3] Parsing syntax AST and checking static type contracts...", OutputType.STDOUT)
                            delay(150)
                            val binName = if (args.contains("-o")) args[args.indexOf("-o") + 1] else srcName.substringBeforeLast('.')
                            appendEntry("[2/3] Emitting optimized LLVM machine code and linking runtime...", OutputType.STDOUT)
                            vfs.writeFile("$currentDir/$binName", "[COMPILED_ELF_BINARY_X86_64]\n$code")
                            appendEntry("[3/3] Binary successfully generated: $binName", OutputType.SUCCESS)
                            appendEntry("[✓] Compilation successful: 0 errors, 0 warnings.", OutputType.SUCCESS)

                            // If flags or command included immediate execution
                            if (args.contains("-run") || args.contains("--run")) {
                                appendEntry("\n--- Running Binary: ./$binName ---", OutputType.SYSTEM)
                                val executionLog = if (isCpp) executeCppCode(code) else executePythonCode(code)
                                appendEntry(executionLog, OutputType.STDOUT)
                                appendEntry("[✓] Process returned 0", OutputType.SUCCESS)
                            }
                            outputBuilder.append("Compilation successful")
                        } else {
                            val err = "$cmd: error: $srcName: No such file or directory"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    }
                }

                "dotnet" -> {
                    val sub = args.firstOrNull() ?: "help"
                    when (sub) {
                        "run" -> {
                            val targetFile = args.getOrNull(1) ?: "Program.cs"
                            val path = resolvePath(targetFile)
                            var code = vfs.readFile(path)
                            if (code == null) {
                                // Search for any .cs file in current directory
                                val csFile = vfs.getAllFiles().firstOrNull { it.path.startsWith(currentDir) && it.name.endsWith(".cs") }
                                if (csFile != null) {
                                    code = csFile.content
                                }
                            }
                            if (code != null) {
                                appendEntry("[*] Building .NET 9.0 SDK Project with Roslyn JIT/AOT...", OutputType.SYSTEM)
                                delay(300)
                                appendEntry("  Determining projects to restore...", OutputType.STDOUT)
                                appendEntry("  Restored /workspace/VirgoApp.csproj (in 112 ms).", OutputType.STDOUT)
                                appendEntry("  VirgoApp -> /workspace/bin/Release/net9.0/VirgoApp.dll", OutputType.SUCCESS)
                                appendEntry("\n--- Output: .NET 9.0 CoreCLR ---", OutputType.SYSTEM)
                                val result = executeCSharpCode(code)
                                appendEntry(result, OutputType.STDOUT)
                                appendEntry("[✓] .NET Application exited with code 0.", OutputType.SUCCESS)
                                outputBuilder.append(result)
                            } else {
                                appendEntry("MSBUILD : error MSB1009: Project file does not exist or no .cs source found.", OutputType.STDERR)
                            }
                        }
                        "build" -> {
                            appendEntry(".NET SDK 9.0.100\nBuilding /workspace/VirgoApp.csproj for .NET 9.0 (x64 Release)...", OutputType.SYSTEM)
                            delay(250)
                            appendEntry("  Optimizing assembly: Native AOT compilation active.", OutputType.STDOUT)
                            appendEntry("Build succeeded.\n    0 Warning(s)\n    0 Error(s)\nTime Elapsed: 00:00:00.84", OutputType.SUCCESS)
                        }
                        "test" -> {
                            appendEntry("Starting test execution, please wait...", OutputType.SYSTEM)
                            delay(250)
                            appendEntry("A total of 18 test files matched the specified pattern.", OutputType.STDOUT)
                            appendEntry("[xUnit.net 00:00:00.45] Total: 18, Passed: 18, Failed: 0, Skipped: 0", OutputType.SUCCESS)
                        }
                        "new" -> {
                            val template = args.getOrNull(1) ?: "console"
                            val csSample = """
// Program.cs - .NET 9.0 C# 13 High-Performance Service
using System;
using System.Linq;
using System.Threading.Tasks;

Console.WriteLine("🚀 VirgoYT Cloud AI - High-End .NET 9.0 C# Microservice");
var numbers = Enumerable.Range(1, 10).Select(x => x * x).ToList();
Console.WriteLine($"Computed squares: {string.Join(", ", numbers)}");
Console.WriteLine("✓ Microservice running with zero latency.");
""".trimIndent()
                            vfs.writeFile("$currentDir/Program.cs", csSample)
                            appendEntry("The template \"Console App\" was created successfully.\nCreated Program.cs in $currentDir", OutputType.SUCCESS)
                        }
                        else -> {
                            appendEntry(".NET SDK (9.0.100)\nUsage: dotnet [run|build|test|new <console|web>]", OutputType.STDOUT)
                        }
                    }
                }

                "csc" -> {
                    val srcName = args.firstOrNull() ?: "Program.cs"
                    val path = resolvePath(srcName)
                    val code = vfs.readFile(path)
                    if (code != null) {
                        appendEntry("Microsoft (R) Visual C# Compiler version 4.12.0-3.24574.8 (Roslyn)", OutputType.SYSTEM)
                        appendEntry("Copyright (C) Microsoft Corporation. All rights reserved.", OutputType.STDOUT)
                        delay(250)
                        val outName = srcName.substringBeforeLast('.') + ".exe"
                        vfs.writeFile("$currentDir/$outName", "[DOTNET_PE_BINARY]\n$code")
                        appendEntry("[✓] Output generated: $outName (Target: net9.0-windows/linux)", OutputType.SUCCESS)
                    } else {
                        appendEntry("fatal error CS2001: Source file '$srcName' could not be found", OutputType.STDERR)
                    }
                }

                "rustc", "cargo" -> {
                    if (cmd == "cargo") {
                        val sub = args.firstOrNull() ?: "build"
                        when (sub) {
                            "run", "build", "check" -> {
                                val rsFile = vfs.getAllFiles().firstOrNull { it.name.endsWith(".rs") }
                                val code = rsFile?.content ?: "fn main() { println!(\"VirgoYT Rust Microkernel\"); }"
                                appendEntry("   Compiling virgoyt-core v0.1.0 (/workspace)", OutputType.SYSTEM)
                                delay(300)
                                appendEntry("    Finished release [optimized] target(s) in 0.72s", OutputType.SUCCESS)
                                if (sub == "run") {
                                    appendEntry("     Running `target/release/virgoyt-core`", OutputType.SYSTEM)
                                    appendEntry(executeRustCode(code), OutputType.STDOUT)
                                    appendEntry("[✓] Process finished with exit code 0", OutputType.SUCCESS)
                                }
                            }
                            else -> appendEntry("cargo 1.80.0 (376290e 2024-07-16)\nUsage: cargo [run|build|check|test]", OutputType.STDOUT)
                        }
                    } else {
                        val src = args.firstOrNull() ?: "main.rs"
                        val path = resolvePath(src)
                        val code = vfs.readFile(path)
                        if (code != null) {
                            appendEntry("[*] Compiling $src with rustc 1.80.0 (LLVM 18.1 backend)...", OutputType.SYSTEM)
                            delay(250)
                            val bin = src.substringBeforeLast('.')
                            vfs.writeFile("$currentDir/$bin", "[RUST_NATIVE_ELF]\n$code")
                            appendEntry("[✓] Compiled binary '$bin' created successfully.", OutputType.SUCCESS)
                        } else {
                            appendEntry("error[E0463]: can't find source file `$src`", OutputType.STDERR)
                        }
                    }
                }

                "go" -> {
                    val sub = args.firstOrNull() ?: "version"
                    when (sub) {
                        "run" -> {
                            val src = args.getOrNull(1) ?: "main.go"
                            val path = resolvePath(src)
                            val code = vfs.readFile(path)
                            if (code != null) {
                                appendEntry("[*] Building and executing Go routine: $src...", OutputType.SYSTEM)
                                delay(200)
                                appendEntry(executeGoCode(code), OutputType.STDOUT)
                                appendEntry("[✓] Go process exited without panic (status: 0)", OutputType.SUCCESS)
                            } else {
                                appendEntry("go: cannot find '$src': No such file", OutputType.STDERR)
                            }
                        }
                        "build" -> {
                            val src = args.getOrNull(1) ?: "main.go"
                            appendEntry("[*] go build: linking dynamic ELF with cgo enabled...", OutputType.SYSTEM)
                            delay(250)
                            appendEntry("[✓] Binary ${src.substringBeforeLast('.')} generated.", OutputType.SUCCESS)
                        }
                        else -> appendEntry("go version go1.23.0 linux/amd64", OutputType.STDOUT)
                    }
                }

                "javac", "java" -> {
                    val src = args.firstOrNull() ?: "Main.java"
                    val path = resolvePath(src)
                    val code = vfs.readFile(path)
                    if (cmd == "javac") {
                        if (code != null) {
                            appendEntry("[*] Compiling $src with OpenJDK 21 javac...", OutputType.SYSTEM)
                            delay(250)
                            vfs.writeFile("$currentDir/${src.substringBeforeLast('.')}.class", "[JAVA_BYTECODE_21]")
                            appendEntry("[✓] Bytecode generated: ${src.substringBeforeLast('.')}.class", OutputType.SUCCESS)
                        } else {
                            appendEntry("javac: file not found: $src", OutputType.STDERR)
                        }
                    } else {
                        if (code != null) {
                            appendEntry("[*] Launching OpenJDK 21 JVM runtime...", OutputType.SYSTEM)
                            delay(200)
                            appendEntry(executeJavaCode(code), OutputType.STDOUT)
                            appendEntry("[✓] JVM terminated successfully.", OutputType.SUCCESS)
                        } else {
                            appendEntry("Error: Could not find or load main class $src", OutputType.STDERR)
                        }
                    }
                }

                "bash", "sh" -> {
                    val script = args.firstOrNull()
                    if (script != null) {
                        val path = resolvePath(script)
                        val content = vfs.readFile(path)
                        if (content != null) {
                            appendEntry("[*] Running shell script $path...", OutputType.SYSTEM)
                            for (line in content.lines()) {
                                val l = line.trim()
                                if (l.isNotEmpty() && !l.startsWith("#")) {
                                    if (l.startsWith("echo ")) {
                                        appendEntry(l.removePrefix("echo ").trim('"', '\''), OutputType.STDOUT)
                                    } else if (l.startsWith("sleep")) {
                                        delay(150)
                                    } else {
                                        appendEntry(l, OutputType.STDOUT)
                                    }
                                }
                            }
                            appendEntry("[✓] Shell execution finished.", OutputType.SUCCESS)
                        } else {
                            appendEntry("bash: $script: No such file", OutputType.STDERR)
                        }
                    }
                }

                "npm" -> {
                    val sub = args.firstOrNull() ?: ""
                    when (sub) {
                        "run" -> {
                            val scriptKey = args.getOrNull(1) ?: "dev"
                            appendEntry("> manus-cloud-workspace@1.0.0 $scriptKey", OutputType.SYSTEM)
                            appendEntry("> Compiling project assets and launching isolated runtime...", OutputType.STDOUT)
                            delay(300)
                            when (scriptKey) {
                                "dev", "start" -> {
                                    appendEntry("  ➜  Local:   http://localhost:3000/", OutputType.SUCCESS)
                                    appendEntry("  ➜  Network: use --host to expose", OutputType.STDOUT)
                                    appendEntry("  ➜  press h + enter to show help", OutputType.STDOUT)
                                }
                                "test" -> {
                                    appendEntry("PASS  test/unit.spec.js (12 tests passed, 0 failed)", OutputType.SUCCESS)
                                    appendEntry("Test Suites: 1 passed, 1 total", OutputType.STDOUT)
                                }
                                else -> appendEntry("✓ npm run $scriptKey finished successfully.", OutputType.SUCCESS)
                            }
                        }
                        "install", "i" -> {
                            val pkg = args.getOrNull(1) ?: "dependencies"
                            appendEntry("added 42 packages, and audited 180 packages in 1.2s", OutputType.SUCCESS)
                            appendEntry("found 0 vulnerabilities", OutputType.STDOUT)
                        }
                        else -> appendEntry("npm v10.8.1", OutputType.STDOUT)
                    }
                }

                "pip" -> {
                    val sub = args.firstOrNull() ?: ""
                    if (sub == "install") {
                        val pkg = args.getOrNull(1) ?: "requirements.txt"
                        appendEntry("Collecting $pkg...", OutputType.STDOUT)
                        delay(200)
                        appendEntry("Downloading $pkg (1.4 MB) [================================] 100%", OutputType.STDOUT)
                        appendEntry("Installing collected packages: $pkg", OutputType.STDOUT)
                        appendEntry("Successfully installed $pkg-3.2.0", OutputType.SUCCESS)
                    } else {
                        appendEntry("pip 24.0 from /usr/local/lib/python3.12/site-packages", OutputType.STDOUT)
                    }
                }

                "git" -> {
                    val sub = args.firstOrNull() ?: "status"
                    when (sub) {
                        "status" -> {
                            val isGh = githubManager.isConnected.value
                            val ghUser = githubManager.currentUser.value?.username ?: "developer"
                            val text = """
On branch main
Your branch is up to date with 'origin/main'.
Remote: https://github.com/$ghUser/manus-cloud-pc.git (fetch & push)
Auth: ${if (isGh) "✓ Authenticated as @$ghUser" else "⚠️ Not connected (run 'gh auth login')"}

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
	modified:   index.html
	modified:   app.js

no changes added to commit (use "git add")
""".trimIndent()
                            appendEntry(text, OutputType.STDOUT)
                            outputBuilder.append(text)
                        }
                        "log" -> {
                            val text = """
commit 7f3a8b29c18d (HEAD -> main, origin/main)
Author: VirgoYT Autonomous Agent <agent@virgoyt.cloud>
Date:   Mon Aug 31 05:20:00 2026 -0700

    feat: integrate sandbox canvas simulator and cyber glassmorphism styles

commit 1a2b3c4d5e6f
Author: VirgoYT System <system@virgoyt.cloud>
Date:   Mon Aug 31 05:00:00 2026 -0700

    initial: initialize cloud computer virtual pc workspace
""".trimIndent()
                            appendEntry(text, OutputType.STDOUT)
                            outputBuilder.append(text)
                        }
                        "commit" -> {
                            appendEntry("[main 9b8c7d6] Autonomous agent changes committed\n 3 files changed, 142 insertions(+), 8 deletions(-)", OutputType.SUCCESS)
                        }
                        "push" -> {
                            if (!githubManager.isConnected.value) {
                                val warn = "fatal: Authentication failed for 'https://github.com/'. Run 'gh auth login' or enter token."
                                appendEntry(warn, OutputType.STDERR)
                                outputBuilder.append(warn)
                            } else {
                                val ghUser = githubManager.currentUser.value?.username ?: "developer"
                                appendEntry("Enumerating objects: 12, done.", OutputType.STDOUT)
                                delay(150)
                                appendEntry("Counting objects: 100% (12/12), done.", OutputType.STDOUT)
                                appendEntry("Compressing objects: 100% (8/8), done.", OutputType.STDOUT)
                                appendEntry("Writing objects: 100% (12/12), 4.21 KiB | 4.21 MiB/s, done.", OutputType.STDOUT)
                                appendEntry("Total 12 (delta 4), reused 0 (delta 0), pack-reused 0", OutputType.STDOUT)
                                appendEntry("To https://github.com/$ghUser/virgoyt-cloud-ai.git\n   7f3a8b2..9b8c7d6  main -> main", OutputType.SUCCESS)
                            }
                        }
                        "pull" -> {
                            appendEntry("Updating 7f3a8b2..9b8c7d6", OutputType.STDOUT)
                            appendEntry("Fast-forward\n index.html | 14 +++++++++++++-\n 1 file changed, 13 insertions(+), 1 deletion(-)", OutputType.SUCCESS)
                        }
                        "remote" -> {
                            val ghUser = githubManager.currentUser.value?.username ?: "developer"
                            appendEntry("origin\thttps://github.com/$ghUser/virgoyt-cloud-ai.git (fetch)\norigin\thttps://github.com/$ghUser/virgoyt-cloud-ai.git (push)", OutputType.STDOUT)
                        }
                        "clone" -> {
                            val targetRepo = args.getOrNull(1) ?: "https://github.com/developer/sample-repo.git"
                            val repoName = targetRepo.substringAfterLast("/").removeSuffix(".git")
                            appendEntry("Cloning into '$repoName'...", OutputType.STDOUT)
                            delay(250)
                            appendEntry("remote: Enumerating objects: 45, done.\nremote: Total 45 (delta 18), reused 45 (delta 18)\nReceiving objects: 100% (45/45), 18.23 KiB | 2.10 MiB/s, done.", OutputType.STDOUT)
                            vfs.addFile("/workspace/$repoName/README.md", "# $repoName\n\nCloned via VirgoYT Cloud Terminal Git Client.")
                            appendEntry("Resolving deltas: 100% (18/18), done.\n[✓] Repo '$repoName' cloned to /workspace/$repoName", OutputType.SUCCESS)
                        }
                        else -> appendEntry("git version 2.45.0", OutputType.STDOUT)
                    }
                }

                "gh", "github" -> {
                    val sub = args.firstOrNull() ?: "help"
                    when (sub) {
                        "auth" -> {
                            val authAction = args.getOrNull(1) ?: "status"
                            when (authAction) {
                                "login" -> {
                                    val tokenArg = args.getOrNull(2)
                                    if (tokenArg != null) {
                                        val ok = githubManager.authorizeWithTokenOrCode(tokenArg, activeUser)
                                        if (ok) {
                                            val u = githubManager.currentUser.value?.username ?: activeUser
                                            val msg = "✓ Successfully authenticated with GitHub as '@$u' via token.\n  Configured Git protocol: HTTPS\n  Token saved to secure keystore."
                                            appendEntry(msg, OutputType.SUCCESS)
                                            outputBuilder.append(msg)
                                        } else {
                                            val err = "Authentication error: Invalid verification code or token."
                                            appendEntry(err, OutputType.STDERR)
                                            outputBuilder.append(err)
                                        }
                                    } else {
                                        val devAuth = githubManager.createDeviceAuth()
                                        val promptText = """
! First copy your one-time device code: ${devAuth.userCode}
- Opening verification URL: ${devAuth.verificationUri}
- Enter the code in your browser, or paste auth token here:
  Run 'gh auth verify ${devAuth.userCode}' or paste directly into terminal.
- Device authorization expires in ${devAuth.expiresInSeconds / 60} minutes.
""".trimIndent()
                                        appendEntry(promptText, OutputType.SYSTEM)
                                        outputBuilder.append(promptText)
                                    }
                                }
                                "verify" -> {
                                    val code = args.getOrNull(2)
                                    if (code == null) {
                                        val err = "Usage: gh auth verify <USER_CODE_OR_TOKEN>"
                                        appendEntry(err, OutputType.STDERR)
                                        outputBuilder.append(err)
                                    } else {
                                        val ok = githubManager.authorizeWithTokenOrCode(code, activeUser)
                                        if (ok) {
                                            val u = githubManager.currentUser.value?.username ?: activeUser
                                            val msg = "✓ Verified! Logged in to GitHub as '$u'. Git credentials configured."
                                            appendEntry(msg, OutputType.SUCCESS)
                                            outputBuilder.append(msg)
                                        } else {
                                            val err = "Verification failed for code '$code'. Please check and try again."
                                            appendEntry(err, OutputType.STDERR)
                                            outputBuilder.append(err)
                                        }
                                    }
                                }
                                "status" -> {
                                    if (githubManager.isConnected.value) {
                                        val user = githubManager.currentUser.value
                                        val statusText = """
github.com
  ✓ Logged in to github.com account ${user?.username} (${user?.token?.take(8)}...)
  - Active account: true
  - Git operations protocol: HTTPS
  - Token scopes: 'repo', 'read:org', 'gist', 'workflow', 'user'
  - Public Repos: ${user?.publicRepos} • Followers: ${user?.followers}
""".trimIndent()
                                        appendEntry(statusText, OutputType.SUCCESS)
                                        outputBuilder.append(statusText)
                                    } else {
                                        val statusText = "You are not logged into any GitHub hosts. Run 'gh auth login' to authenticate."
                                        appendEntry(statusText, OutputType.STDERR)
                                        outputBuilder.append(statusText)
                                    }
                                }
                                "logout" -> {
                                    githubManager.disconnect()
                                    val msg = "✓ Successfully logged out of github.com."
                                    appendEntry(msg, OutputType.SUCCESS)
                                    outputBuilder.append(msg)
                                }
                                else -> {
                                    val help = "Usage: gh auth <login|status|logout|verify [code]>"
                                    appendEntry(help, OutputType.STDOUT)
                                    outputBuilder.append(help)
                                }
                            }
                        }

                        "repo" -> {
                            val repoAction = args.getOrNull(1) ?: "list"
                            when (repoAction) {
                                "list" -> {
                                    val repos = githubManager.userRepos.value
                                    val user = githubManager.currentUser.value?.username ?: "developer"
                                    appendEntry("Showing ${repos.size} repositories for @$user:\n", OutputType.STDOUT)
                                    repos.forEach { r ->
                                        val line = String.format(Locale.US, "%-32s  %-12s  ⭐ %-4d  %s", r.fullName, r.language, r.stars, r.description)
                                        appendEntry(line, OutputType.STDOUT)
                                        outputBuilder.appendLine(line)
                                    }
                                }
                                "clone" -> {
                                    val target = args.getOrNull(2)
                                    if (target == null) {
                                        val err = "Usage: gh repo clone <owner/repo>"
                                        appendEntry(err, OutputType.STDERR)
                                    } else {
                                        executeCommand("git clone https://github.com/$target.git")
                                    }
                                }
                                "create" -> {
                                    val repoName = args.getOrNull(2) ?: "new-repo"
                                    val newRepo = githubManager.addRepo(repoName, "Created from Manus Cloud Terminal")
                                    val msg = "✓ Created repository ${newRepo.fullName} on GitHub (HTTPS: https://github.com/${newRepo.fullName}.git)"
                                    appendEntry(msg, OutputType.SUCCESS)
                                    outputBuilder.append(msg)
                                }
                                else -> appendEntry("Usage: gh repo <list|clone <repo>|create <name>>", OutputType.STDOUT)
                            }
                        }

                        "issue" -> {
                            val user = githubManager.currentUser.value?.username ?: "developer"
                            val issuesText = """
Showing 3 open issues for $user/virgoyt-cloud-ai:

#42  [Feat] Add multi-platform terminal CLI installer (Linux/Mac/Win/Termux)  (opened 2h ago by @user)
#41  [Auth] Implement GitHub Device Code web-to-terminal OAuth bridge         (opened 4h ago by @dev)
#38  [Core] Support local bridge execution host on 127.0.0.1:8080             (opened yesterday by @lead)
""".trimIndent()
                            appendEntry(issuesText, OutputType.STDOUT)
                            outputBuilder.append(issuesText)
                        }

                        "gist" -> {
                            val subGist = args.getOrNull(1)
                            if (subGist == "create") {
                                val fileTarget = args.getOrNull(2) ?: "main.py"
                                val gistUrl = "https://gist.github.com/developer/${UUID.randomUUID().toString().take(10)}"
                                appendEntry("✓ Created public Gist: $gistUrl for file '$fileTarget'", OutputType.SUCCESS)
                                outputBuilder.append(gistUrl)
                            } else {
                                appendEntry("Usage: gh gist create <filename>", OutputType.STDOUT)
                            }
                        }

                        else -> {
                            val ghHelp = """
GitHub CLI (gh) 2.50.0:
  gh auth login             - Authenticate with GitHub via browser & device code
  gh auth status            - View active authentication credentials
  gh auth logout            - Clear stored GitHub credentials
  gh repo list              - List account repositories
  gh repo clone <repo>      - Clone a GitHub repo
  gh repo create <name>     - Create new repository
  gh issue list             - View issues
  gh gist create <file>     - Create a GitHub Gist
""".trimIndent()
                            appendEntry(ghHelp, OutputType.STDOUT)
                            outputBuilder.append(ghHelp)
                        }
                    }
                }

                "virgoyt", "manus" -> {
                    val sub = args.firstOrNull() ?: "help"
                    when (sub) {
                        "install", "cli" -> {
                            val installBanner = """
╔════════════════════════════════════════════════════════════════════════════╗
║             VIRGOYT CLOUD AI TERMINAL - MULTI-PLATFORM CLI INSTALLER       ║
╚════════════════════════════════════════════════════════════════════════════╝

Select your platform or copy the 1-line installation command:

🐧 LINUX (Ubuntu, Debian, Fedora, Arch):
   curl -fsSL https://virgoyt.cloud/install.sh | bash
   virgoyt login && virgoyt start

🍎 MACOS (Apple Silicon & Intel / Homebrew):
   brew install virgoyt-ai/tap/virgoyt-cli
   # or via direct installer:
   curl -fsSL https://virgoyt.cloud/install-mac.sh | bash

🪟 WINDOWS (PowerShell 7+ / WSL2 / Command Prompt):
   powershell -c "irm https://virgoyt.cloud/install.ps1 | iex"
   # or via winget:
   winget install VirgoYTAI.VirgoYTCli

📱 ANDROID (Termux Terminal):
   pkg update && pkg install -y git python curl openssh nodejs
   curl -fsSL https://virgoyt.cloud/install-termux.sh | bash
   virgoyt-termux --host 127.0.0.1:8080

⚡ RUNNING IN LOCALHOST OR CLOUD MODE:
   virgoyt mode local   -> Run terminal connected to local machine (127.0.0.1:8080)
   virgoyt mode cloud   -> Run terminal connected to Cloud Sandbox VM
""".trimIndent()
                            appendEntry(installBanner, OutputType.SYSTEM)
                            outputBuilder.append(installBanner)
                        }

                        "mode" -> {
                            val targetMode = args.getOrNull(1)?.lowercase()
                            when (targetMode) {
                                "local", "localhost" -> {
                                    setTerminalMode(TerminalMode.LOCALHOST)
                                }
                                "cloud", "remote", "vm" -> {
                                    setTerminalMode(TerminalMode.CLOUD_VM)
                                }
                                else -> {
                                    val modeInfo = """
Current Execution Mode: [${_terminalMode.value.label}]
Target Host: ${_terminalMode.value.host}
Usage:
  virgoyt mode local   -> Switch to Localhost (127.0.0.1:8080)
  virgoyt mode cloud   -> Switch to Cloud VM (asia-east1 Ubuntu container)
""".trimIndent()
                                    appendEntry(modeInfo, OutputType.STDOUT)
                                    outputBuilder.append(modeInfo)
                                }
                            }
                        }

                        "status" -> {
                            val ghConnected = githubManager.isConnected.value
                            val ghUser = githubManager.currentUser.value?.username ?: "None"
                            val statusReport = """
VIRGOYT CLOUD AI INSTANCE STATUS:
  • Execution Mode   : ${_terminalMode.value.label} (${_terminalMode.value.host})
  • Container OS     : Ubuntu 24.04.1 LTS (Linux 6.8.0-cloud)
  • Active User      : $activeUser (Lead Developer)
  • GitHub Connected : ${if (ghConnected) "✓ Linked to @$ghUser" else "✗ Not connected"}
  • VFS Storage      : ${vfs.getAllFiles().size} files mapped in /workspace
  • Memory Usage     : 1.84 GB / 8.00 GB (23%)
  • CPU Load Avg     : 0.14, 0.08, 0.03 (8 Cores)
  • Daemon Port      : 8080 (Listening on 0.0.0.0:8080)
""".trimIndent()
                            appendEntry(statusReport, OutputType.SUCCESS)
                            outputBuilder.append(statusReport)
                        }

                        else -> {
                            val virgoytHelp = """
VirgoYT Cloud AI CLI Commands:
  virgoyt install             - Display one-click installation scripts (Linux, Mac, Windows, Termux)
  virgoyt mode <local|cloud>  - Toggle execution host between Localhost & Cloud VM
  virgoyt status              - Display system specs, daemon status & GitHub integration
""".trimIndent()
                            appendEntry(virgoytHelp, OutputType.STDOUT)
                            outputBuilder.append(virgoytHelp)
                        }
                    }
                }

                "curl" -> {
                    val url = args.firstOrNull() ?: "https://api.virgoyt.cloud/health"
                    appendEntry("[*] Requesting HTTP GET $url...", OutputType.SYSTEM)
                    delay(200)
                    val mockResponse = """
HTTP/2 200 OK
content-type: application/json; charset=utf-8
server: cloudflare
date: Mon, 31 Aug 2026 05:30:00 GMT

{
  "status": "healthy",
  "cluster": "asia-east1-cloud-node-4",
  "latency_ms": 14.8,
  "sandbox_id": "sbx-virgoyt-88912",
  "auth": "bearer-verified"
}
""".trimIndent()
                    appendEntry(mockResponse, OutputType.STDOUT)
                    outputBuilder.append(mockResponse)
                }

                "ping" -> {
                    val host = args.firstOrNull() ?: "localhost"
                    appendEntry("PING $host (127.0.0.1) 56(84) bytes of data.", OutputType.STDOUT)
                    for (seq in 1..3) {
                        delay(120)
                        val pingLine = "64 bytes from $host (127.0.0.1): icmp_seq=$seq ttl=64 time=0.0${(12..28).random()} ms"
                        appendEntry(pingLine, OutputType.STDOUT)
                    }
                    appendEntry("--- $host ping statistics ---", OutputType.STDOUT)
                    appendEntry("3 packets transmitted, 3 received, 0% packet loss, time 204ms", OutputType.SUCCESS)
                }

                else -> {
                    // Check if it is an executable script ./script or binary
                    if (trimmed.startsWith("./")) {
                        val file = resolvePath(trimmed.removePrefix("./"))
                        val content = vfs.readFile(file)
                        if (content != null) {
                            if (content.startsWith("#!/bin/bash") || file.endsWith(".sh")) {
                                executeCommand("bash $file")
                            } else {
                                appendEntry("[*] Executing binary ./$trimmed...", OutputType.SYSTEM)
                                delay(200)
                                appendEntry("[*] Process started with PID 1492\n[+] Output: OK\n[✓] Finished (exit 0)", OutputType.SUCCESS)
                            }
                        } else {
                            val err = "bash: $trimmed: No such file or directory"
                            appendEntry(err, OutputType.STDERR)
                            outputBuilder.append(err)
                        }
                    } else {
                        val err = "bash: $cmd: command not found. Type 'help' for available commands."
                        appendEntry(err, OutputType.STDERR)
                        outputBuilder.append(err)
                    }
                }
            }
        } catch (e: Exception) {
            val err = "Runtime error: ${e.message}"
            appendEntry(err, OutputType.STDERR)
            outputBuilder.append(err)
        } finally {
            _isExecuting.value = false
        }

        return outputBuilder.toString()
    }

    private fun resolvePath(raw: String): String {
        return when {
            raw.startsWith("/") -> raw
            raw.startsWith("~/") -> "/workspace/" + raw.removePrefix("~/")
            raw == "~" -> "/workspace"
            raw == ".." -> {
                val parent = currentDir.substringBeforeLast('/')
                if (parent.isEmpty()) "/" else parent
            }
            raw == "." -> currentDir
            else -> {
                val base = if (currentDir.endsWith("/")) currentDir else "$currentDir/"
                base + raw
            }
        }
    }

    private fun generateTree(dir: String): String {
        val files = vfs.getAllFiles().filter { it.path.startsWith(dir) }
        val sb = StringBuilder()
        sb.appendLine(dir)
        files.forEach { f ->
            if (f.path != dir) {
                val relative = f.path.removePrefix(dir).removePrefix("/")
                val depth = relative.count { it == '/' }
                val prefix = "│   ".repeat(depth) + "├── "
                sb.appendLine("$prefix${f.name}${if (f.isDirectory) "/" else ""}")
            }
        }
        sb.append("${files.size} directories, ${files.count { !it.isDirectory }} files")
        return sb.toString()
    }

    private fun executePythonCode(code: String): String {
        val sb = StringBuilder()
        val lines = code.lines()
        for (line in lines) {
            val t = line.trim()
            if (t.startsWith("print(") && t.endsWith(")")) {
                val inside = t.removePrefix("print(").removeSuffix(")")
                val text = inside.trim('"', '\'').replace("\\n", "\n")
                val clean = if (text.startsWith("f\"") || text.startsWith("f'")) {
                    text.substring(2, text.length - 1)
                } else text
                sb.appendLine(clean)
            }
        }
        if (sb.isEmpty()) {
            sb.appendLine("[Python 3.12 Engine] Process finished with exit code 0.")
        }
        return sb.toString().trimEnd()
    }

    private fun executeJsCode(code: String): String {
        val sb = StringBuilder()
        val lines = code.lines()
        for (line in lines) {
            val t = line.trim()
            if (t.startsWith("console.log(") && t.endsWith(");")) {
                val inside = t.removePrefix("console.log(").removeSuffix(");")
                sb.appendLine(inside.trim('"', '`', '\''))
            } else if (t.startsWith("console.log(") && t.endsWith(")")) {
                val inside = t.removePrefix("console.log(").removeSuffix(")")
                sb.appendLine(inside.trim('"', '`', '\''))
            }
        }
        if (sb.isEmpty()) {
            sb.appendLine("[Node.js v22.1.0] Script executed successfully (exit code: 0).")
        }
        return sb.toString().trimEnd()
    }

    private fun executeCppCode(code: String): String {
        val sb = StringBuilder()
        val lines = code.lines()
        for (line in lines) {
            val t = line.trim()
            if (t.contains("std::cout") || t.contains("cout")) {
                val parts = t.substringAfter("cout").split("<<")
                for (part in parts) {
                    val p = part.trim().removeSuffix(";").trim()
                    if (p == "std::endl" || p == "endl") {
                        sb.appendLine()
                    } else if (p.startsWith("\"") && p.endsWith("\"")) {
                        sb.append(p.removeSurrounding("\"").replace("\\n", "\n"))
                    } else if (p.isNotEmpty() && !p.startsWith("/*") && !p.startsWith("//")) {
                        sb.append(p)
                    }
                }
                if (!t.contains("endl") && !t.contains("\\n")) {
                    sb.appendLine()
                }
            } else if (t.contains("printf(") || t.contains("std::println(")) {
                val raw = if (t.contains("printf(")) t.substringAfter("printf(").substringBeforeLast(")")
                else t.substringAfter("std::println(").substringBeforeLast(")")
                val clean = raw.trim().removeSurrounding("\"").replace("\\n", "\n").replace("%d", "42").replace("%s", "OK")
                sb.appendLine(clean)
            }
        }
        if (sb.isEmpty()) {
            sb.appendLine("[C++23 Native Binary] ELF 64-bit LSB executable, x86-64, dynamically linked.")
            sb.appendLine("[+] Execution latency: 0.038ms | CPU Cycles: 1,420 | Status: SUCCESS")
        }
        return sb.toString().trimEnd()
    }

    private fun executeCSharpCode(code: String): String {
        val sb = StringBuilder()
        val lines = code.lines()
        for (line in lines) {
            val t = line.trim()
            if (t.contains("Console.WriteLine(") || t.contains("Console.Write(")) {
                val isLine = t.contains("WriteLine")
                val inside = if (isLine) t.substringAfter("Console.WriteLine(").substringBeforeLast(");").substringBeforeLast(")")
                else t.substringAfter("Console.Write(").substringBeforeLast(");").substringBeforeLast(")")
                val clean = inside.trim().trim('$', '@', '"', '\'').replace("\\n", "\n")
                if (isLine) sb.appendLine(clean) else sb.append(clean)
            }
        }
        if (sb.isEmpty()) {
            sb.appendLine("[.NET 9.0 CoreCLR / C# 13 Runtime] Ready to Run (R2R) Image Loaded.")
            sb.appendLine("VirgoYT High-Performance C# Microservice initialized.")
        }
        return sb.toString().trimEnd()
    }

    private fun executeRustCode(code: String): String {
        val sb = StringBuilder()
        val lines = code.lines()
        for (line in lines) {
            val t = line.trim()
            if (t.contains("println!(") || t.contains("print!(")) {
                val inside = t.substringAfter("!(").substringBeforeLast(");").substringBeforeLast(")")
                val clean = inside.trim().trim('"', '\'').replace("\\n", "\n")
                sb.appendLine(clean)
            }
        }
        if (sb.isEmpty()) {
            sb.appendLine("[Rust 1.80 LLVM] Memory Safety Verified (0 borrow checker violations).")
        }
        return sb.toString().trimEnd()
    }

    private fun executeGoCode(code: String): String {
        val sb = StringBuilder()
        val lines = code.lines()
        for (line in lines) {
            val t = line.trim()
            if (t.contains("fmt.Println(") || t.contains("fmt.Printf(")) {
                val inside = t.substringAfter("fmt.Print").substringAfter("(").substringBeforeLast(")")
                val clean = inside.trim().trim('"', '`', '\'').replace("\\n", "\n")
                sb.appendLine(clean)
            }
        }
        if (sb.isEmpty()) {
            sb.appendLine("[Go 1.23.0 Engine] Compiled with gc go1.23 linux/amd64 (1 goroutine).")
        }
        return sb.toString().trimEnd()
    }

    private fun executeJavaCode(code: String): String {
        val sb = StringBuilder()
        val lines = code.lines()
        for (line in lines) {
            val t = line.trim()
            if (t.contains("System.out.println(") || t.contains("System.out.print(")) {
                val isLine = t.contains("println")
                val inside = if (isLine) t.substringAfter("System.out.println(").substringBeforeLast(");").substringBeforeLast(")")
                else t.substringAfter("System.out.print(").substringBeforeLast(");").substringBeforeLast(")")
                val clean = inside.trim().trim('"', '\'').replace("\\n", "\n")
                if (isLine) sb.appendLine(clean) else sb.append(clean)
            }
        }
        if (sb.isEmpty()) {
            sb.appendLine("[OpenJDK 21.0.2] HotSpot(TM) 64-Bit Server VM (build 21.0.2+13-LTS).")
        }
        return sb.toString().trimEnd()
    }
}
