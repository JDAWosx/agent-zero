#!/bin/bash
# Agent Zero Android/Termux Installation Script

echo "=========================================="
echo "Agent Zero for Android/Termux Installer"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if we're in Termux
if [ ! -d "$PREFIX" ]; then
    echo -e "${RED}Error: This script is designed for Termux on Android${NC}"
    echo "Please install Termux from F-Droid or Google Play"
    exit 1
fi

echo -e "${GREEN}✓ Detected Termux environment${NC}"
echo ""

# Update packages
echo -e "${YELLOW}[1/5] Updating Termux packages...${NC}"
pkg update -y
pkg upgrade -y

# Install Python and development tools
echo -e "${YELLOW}[2/5] Installing Python and development tools...${NC}"
pkg install -y python
pkg install -y python-pip
pkg install -y git
pkg install -y wget
pkg install -y curl
pkg install -y openssh
pkg install -y cmake
pkg install -y rust

# Install Python packages that work well on Termux
echo -e "${YELLOW}[3/5] Installing core Python packages...${NC}"

# Upgrade pip first
pip install --upgrade pip setuptools wheel

# Install core dependencies with better error handling
pip install flask flask-basicauth || {
    echo -e "${YELLOW}Warning: flask-basicauth installation failed, continuing...${NC}"
}

# Install AI/ML dependencies
echo -e "${YELLOW}Installing AI/ML dependencies (this may take a while)...${NC}"
pip install litellm langchain-core langchain-community || {
    echo -e "${YELLOW}Warning: Some AI/ML dependencies failed to install${NC}"
}

# Install utility packages
pip install duckduckgo-search psutil python-dotenv pytz requests simpleeval || {
    echo -e "${YELLOW}Warning: Some utility packages failed to install${NC}"
}

# Install MCP and A2A (Agent-to-Agent) support
echo -e "${YELLOW}Installing MCP and A2A support...${NC}"
pip install fastmcp fasta2a mcp || {
    echo -e "${YELLOW}Warning: MCP/A2A support may not be fully available${NC}"
}

# Install optional dependencies
echo -e "${YELLOW}[4/5] Installing optional dependencies...${NC}"
pip install fastapi uvicorn GitPython markdown webcolors || {
    echo -e "${YELLOW}Note: Some optional packages failed, core features will still work${NC}"
}

# Install Playwright for browser automation
echo -e "${YELLOW}Installing Playwright for browser automation...${NC}"
pip install playwright 2>/dev/null || {
    echo -e "${YELLOW}Warning: Playwright not available via pip${NC}"
    echo -e "${YELLOW}Will use system Chromium if installed${NC}"
}

# Install Playwright browsers (if Playwright is installed)
if command -v playwright &> /dev/null; then
    echo -e "${YELLOW}Installing Playwright browser binaries...${NC}"
    playwright install chromium --with-deps 2>/dev/null || {
        echo -e "${YELLOW}Warning: Playwright browser installation failed${NC}"
        echo -e "${YELLOW}You can install Chromium via: pkg install chromium${NC}"
    }
fi

# Create directories
echo -e "${YELLOW}[5/5] Creating directories...${NC}"
mkdir -p logs
mkdir -p memory
mkdir -p knowledge
mkdir -p tmp

# Download models if needed
echo ""
echo -e "${GREEN}Creating environment file...${NC}"
cat > .env << 'EOF'
# Agent Zero Android/Termux Configuration
# Edit these values to configure your AI models

# Chat Model Configuration (Main conversation model)
CHAT_MODEL_PROVIDER=openai
CHAT_MODEL_NAME=gpt-3.5-turbo
CHAT_MODEL_API_BASE=
CHAT_MODEL_API_KEY=
CHAT_MODEL_VISION=false
CHAT_MODEL_CTX_LENGTH=0
CHAT_MODEL_RL_REQUESTS=10
CHAT_MODEL_RL_INPUT=100000
CHAT_MODEL_RL_OUTPUT=100000
CHAT_MODEL_KWARGS={}

# Utility Model (for internal tasks like summarization, code analysis)
UTIL_MODEL_PROVIDER=openai
UTIL_MODEL_NAME=gpt-3.5-turbo
UTIL_MODEL_API_BASE=
UTIL_MODEL_API_KEY=
UTIL_MODEL_CTX_LENGTH=0
UTIL_MODEL_RL_REQUESTS=20
UTIL_MODEL_RL_INPUT=50000
UTIL_MODEL_RL_OUTPUT=50000
UTIL_MODEL_KWARGS={}

# Embedding Model (for knowledge base and semantic search)
EMBED_MODEL_PROVIDER=openai
EMBED_MODEL_NAME=text-embedding-3-small
EMBED_MODEL_API_BASE=
EMBED_MODEL_API_KEY=
EMBED_MODEL_RL_REQUESTS=10
EMBED_MODEL_RL_INPUT=10000
EMBED_MODEL_RL_OUTPUT=10000
EMBED_MODEL_KWARGS={}

# Browser Model (for browser automation tasks)
BROWSER_MODEL_PROVIDER=openai
BROWSER_MODEL_NAME=gpt-3.5-turbo
BROWSER_MODEL_API_BASE=
BROWSER_MODEL_API_KEY=
BROWSER_MODEL_VISION=true
BROWSER_MODEL_KWARGS={}
BROWSER_HTTP_HEADERS={}

# MCP (Model Context Protocol) Configuration
# For connecting to external MCP servers
MCP_SERVERS=[]

# Security Token for API access
MCP_SERVER_TOKEN=your-secret-token-here-change-this

# Android/Termux Agent Settings
AGENT_PROFILE=default
AGENT_MEMORY_SUBDIR=android
AGENT_KNOWLEDGE_SUBDIR=android

# Legacy/Backward Compatibility
CHAT_MODEL_KWARGS={}
EOF

echo ""
echo -e "${GREEN}==========================================${NC}"
echo -e "${GREEN}Installation Complete!${NC}"
echo -e "${GREEN}==========================================${NC}"
echo ""
echo "Next steps:"
echo "1. Edit .env file with your API keys:"
echo "   nano .env"
echo ""
echo "2. Start Agent Zero:"
echo "   python run_ui.py"
echo ""
echo "3. Access the web interface at:"
echo "   http://localhost:50001"
echo ""
echo -e "${YELLOW}Note on Android limitations:${NC}"
echo "- Browser automation tools are disabled on Android"
echo "- PDF/document processing may be limited"
echo "- Heavy ML models may be slow"
echo "- Some advanced features may not work"
echo ""
echo -e "${YELLOW}For help and troubleshooting:${NC}"
echo "- Visit: https://github.com/agent0ai/agent-zero"
echo "- Discord: https://discord.gg/B8KZKNsPpj"
echo ""
