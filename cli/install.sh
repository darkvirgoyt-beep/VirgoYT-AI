#!/usr/bin/env bash

# ==============================================================================
# VirgoYT AI - 1-Line Termux & Linux CLI Agent Harness Installer
# Like Claude Code and DeepSeek Harness for your Android Terminal
# ==============================================================================

set -e

GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${CYAN}⚡ Installing VirgoYT Terminal Coding Agent Harness...${NC}"

# Detect if Termux
if [ -n "$TERMUX_VERSION" ] || [ -d "/data/data/com.termux" ]; then
    echo -e "${YELLOW}📱 Termux Android environment detected!${NC}"
    echo -e "${CYAN}→ Installing Node.js & Git (No heavy Java/OpenJDK needed)...${NC}"
    pkg update -y
    pkg install -y nodejs git curl
    BIN_DIR="$PREFIX/bin"
else
    echo -e "${CYAN}🐧 Linux / macOS environment detected!${NC}"
    BIN_DIR="/usr/local/bin"
    if [ ! -w "$BIN_DIR" ]; then
        BIN_DIR="$HOME/.local/bin"
        mkdir -p "$BIN_DIR"
    fi
fi

# Target installation path
TARGET_SCRIPT="$BIN_DIR/virgoyt"

echo -e "${CYAN}→ Downloading VirgoYT CLI harness engine...${NC}"
curl -sSL "https://raw.githubusercontent.com/darkvirgoyt-beep/VirgoYT-AI/main/cli/virgoyt.js" -o "$TARGET_SCRIPT" || {
    # Fallback to local copy if running inside cloned repo
    if [ -f "$(dirname "$0")/virgoyt.js" ]; then
        cp "$(dirname "$0")/virgoyt.js" "$TARGET_SCRIPT"
    fi
}

chmod +x "$TARGET_SCRIPT"

echo -e "${GREEN}✓ Successfully installed VirgoYT CLI to $TARGET_SCRIPT${NC}"
echo -e ""
echo -e "${CYAN}════════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}🚀 Ready to code! Start your terminal coding agent with:${NC}"
echo -e "   ${YELLOW}virgoyt${NC}"
echo -e "${CYAN}════════════════════════════════════════════════════════════════${NC}"
echo -e ""
echo -e "Optional: Set your Gemini API key for live deep thinking:"
echo -e "   ${CYAN}export GEMINI_API_KEY='your_api_key'${NC}"
echo -e ""

# Run immediately if terminal is interactive
if [ -t 0 ]; then
    "$TARGET_SCRIPT"
fi
