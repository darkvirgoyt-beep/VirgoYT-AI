#!/usr/bin/env python3
"""
VirgoYT AI - Python Terminal Coding Agent Harness
Equivalent to Claude Code and DeepSeek Terminal Harness for Termux & Linux
"""

import os
import sys
import subprocess
import readline

CYAN = "\033[36m"
GREEN = "\033[32m"
YELLOW = "\033[33m"
MAGENTA = "\033[35m"
BOLD = "\033[1m"
RESET = "\033[0m"

def banner():
    os.system("clear" if os.name != "nt" else "cls")
    print(f"{CYAN}{BOLD}╭─────────────────────────────────────────────────────────────╮{RESET}")
    print(f"{CYAN}{BOLD}│              ⚡ VIRGOYT AI PYTHON HARNESS (CLI)             │{RESET}")
    print(f"{CYAN}{BOLD}│         Interactive Coding Agent for Termux & Linux         │{RESET}")
    print(f"{CYAN}{BOLD}╰─────────────────────────────────────────────────────────────╯{RESET}")
    print(f"Working Directory: {os.getcwd()}")
    print(f"Commands: {CYAN}/help{RESET}, {CYAN}/run <cmd>{RESET}, {CYAN}/diff{RESET}, {CYAN}/exit{RESET}\n")

def main():
    banner()
    while True:
        try:
            cmd = input(f"{CYAN}{BOLD}virgoyt > {RESET}").strip()
            if not cmd:
                continue
            if cmd in ["/exit", "exit", "quit"]:
                print(f"{GREEN}Goodbye! Happy coding! 🚀{RESET}")
                break
            elif cmd == "/help":
                print("\nCommands:")
                print("  /help       Show help")
                print("  /run <cmd>  Execute bash command in Termux")
                print("  /diff       Git diff")
                print("  /clear      Clear screen")
                print("  /exit       Quit\n")
            elif cmd == "/clear":
                banner()
            elif cmd.startswith("/run "):
                subprocess.run(cmd[5:], shell=True)
            elif cmd == "/diff":
                subprocess.run("git diff", shell=True)
            else:
                print(f"{MAGENTA}{BOLD}🧠 Chain-of-Thought:{RESET} Reasoning over prompt in Termux environment...")
                print(f"{BOLD}VirgoYT:{RESET} Ready to assist in Termux! Run bash commands with /run <cmd>.")
        except (KeyboardInterrupt, EOFError):
            print("\nExiting...")
            break

if __name__ == "__main__":
    main()
