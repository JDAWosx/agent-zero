# Agent Zero for Android/Termux

This is a **modified version of Agent Zero** that runs on Android devices using Termux.

## What is Agent Zero?

Agent Zero is a personal, organic agentic AI framework that:
- Acts as a general-purpose AI assistant
- Can execute code and terminal commands
- Has persistent memory
- Supports multi-agent cooperation
- Features a web-based UI
- Is fully customizable through prompts and tools

## Android/Termux Compatibility

This version has been modified to work on Android with the following changes:

### ✅ What's Working on Android
- Core agent functionality
- Web UI via Flask (accessible at http://localhost:50001)
- AI model integration via LiteLLM (OpenAI, Anthropic, local models, etc.)
- **Browser automation** - Full Playwright/browser-use integration
- **MCP Protocol** - Connect to external MCP servers
- **A2A Protocol** - Agent-to-agent communication
- Code execution (Python, Node.js, terminal commands)
- Memory and knowledge management
- Search tools (DuckDuckGo)
- Scheduler and task automation
- Extensible tool system

### ⚠️ Android-Specific Considerations
- **PDF processing** - Some document processing tools may require additional dependencies
- **Heavy ML models** - Local models may be slow due to device constraints
- **Docker** - Not available, code execution uses local shell (safe on Android)
- **TTS/STT** - Speech features may not work on all devices
- **ARM Architecture** - Optimized for ARM64 (most Android devices)

## Installation

### Prerequisites
1. **Install Termux** on your Android device:
   - F-Droid (recommended): https://f-droid.org/packages/com.termux/
   - Google Play Store

2. **Update Termux packages:**
   ```bash
   pkg update && pkg upgrade
   ```

### Installation Steps

1. **Clone this repository:**
   ```bash
   git clone https://github.com/JDAWosx/agent-zero.git
   cd agent-zero
   ```

2. **Run the Android installation script:**
   ```bash
   bash install-android.sh
   ```

3. **Configure your API keys:**
   ```bash
   nano .env
   ```

   Edit the `.env` file and add your API keys:
   ```
   CHAT_MODEL_API_KEY=your_openai_api_key_here
   UTIL_MODEL_API_KEY=your_openai_api_key_here
   EMBED_MODEL_API_KEY=your_openai_api_key_here
   BROWSER_MODEL_API_KEY=your_openai_api_key_here
   MCP_SERVER_TOKEN=your-secret-token-here
   ```

   Save and exit (Ctrl+X, then Y, then Enter)

4. **Start Agent Zero:**
   ```bash
   bash start-android.sh
   ```

5. **Access the web interface:**
   - Open your browser on Android
   - Go to: `http://localhost:50001`
   - Enter the MCP_SERVER_TOKEN from your .env file when prompted

## Configuration

### Environment Variables (.env)

Key configuration options:

```bash
# AI Model Configuration
CHAT_MODEL_PROVIDER=openai              # openai, anthropic, ollama, etc.
CHAT_MODEL_NAME=gpt-3.5-turbo          # Model name
CHAT_MODEL_API_KEY=your_key_here       # API key

# Other models (utility, embeddings, browser)
UTIL_MODEL_PROVIDER=openai
UTIL_MODEL_NAME=gpt-3.5-turbo
EMBED_MODEL_PROVIDER=openai
EMBED_MODEL_NAME=text-embedding-3-small

# Security
MCP_SERVER_TOKEN=your-secret-token      # Required for API access

# Android-specific settings
AGENT_PROFILE=default
AGENT_MEMORY_SUBDIR=android
AGENT_KNOWLEDGE_SUBDIR=android
```

### Model Providers

Agent Zero uses **LiteLLM** to support multiple AI providers:

- **OpenAI**: GPT-3.5, GPT-4, GPT-4o
- **Anthropic**: Claude 3 (Haiku, Sonnet, Opus)
- **Ollama**: Local models (Llama, Mistral, etc.)
- **Google**: Gemini
- **Local providers**: Via OpenAI-compatible APIs

For local models, you can use **Ollama**:
```bash
# Install Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Pull a model
ollama pull llama2
ollama pull mistral

# Update .env for local models
CHAT_MODEL_PROVIDER=openai
CHAT_MODEL_NAME=ollama/llama2
CHAT_MODEL_API_BASE=http://localhost:11434
```

## Usage

### Starting Agent Zero
```bash
bash start-android.sh
```

### Accessing the Web UI
1. Open your browser
2. Navigate to `http://localhost:50001`
3. Enter your MCP_SERVER_TOKEN when prompted

### Using the Agent

Once connected, you can:
- **Chat with the agent** - Ask questions or give tasks
- **Execute code** - Python, Node.js, or terminal commands
- **Save memories** - The agent remembers past conversations
- **Use tools** - Search, file operations, etc.
- **Access settings** - Customize behavior via the settings panel

### Browser Automation on Android

Agent Zero includes powerful browser automation capabilities that work on Android:

**Installing Browser Support:**
```bash
# Option 1: Install Chromium via Termux (recommended)
pkg install chromium

# Option 2: Install Playwright (if available)
pip install playwright
playwright install chromium
```

**Browser Automation Features:**
- Navigate websites and interact with JavaScript-heavy sites
- Extract data from web pages
- Automate form filling and submissions
- Screenshot and visual analysis
- Handle complex user interactions

**Android-Specific Browser Settings:**
The browser agent is optimized for Android with:
- ARM-compatible Chromium binaries
- Graceful fallback to system browser if Playwright isn't available
- Headless mode for faster execution
- Reduced memory footprint

**Example Browser Automation Tasks:**
```
"Browse to example.com and extract all article titles"
"Search for 'AI news' on Google and summarize the top 3 results"
"Fill out a contact form on a website"
"Take a screenshot of a specific webpage"
"Navigate an e-commerce site and find the cheapest laptop"
```

### MCP (Model Context Protocol) Support

Agent Zero includes full support for MCP (Model Context Protocol), allowing you to:

**Connect to External MCP Servers:**
- Extend functionality with external tools
- Integrate with MCP-compatible services
- Use specialized MCP servers for specific tasks

**MCP Configuration:**
Edit your `.env` file:
```bash
# Example MCP server configuration
MCP_SERVERS=[
    {
        "name": "my-mcp-server",
        "url": "http://localhost:3000",
        "type": "http-stream"
    }
]
```

**A2A (Agent-to-Agent) Protocol:**
Agent Zero also supports A2A for:
- Multi-agent communication
- Distributed task execution
- Agent collaboration

### Example Commands
```
"Analyze this image and tell me what you see"
"Write a Python script to calculate fibonacci numbers"
"Search for information about quantum computing"
"Create a simple web page with HTML/CSS"
"Schedule a reminder for tomorrow at 2pm"
"Browse to news.ycombinator.com and summarize top stories"
"Navigate to my bank website and check account balance"
```

## Troubleshooting

### Installation Issues

**Error: "package not found"**
```bash
# Update package lists
pkg update -y

# Try installing packages individually
pkg install python git wget curl
```

**Error: "pip install failed"**
```bash
# Upgrade pip first
pip install --upgrade pip setuptools wheel

# Install packages without dependencies
pip install --no-deps package_name
```

**Error: "Permission denied"**
```bash
# Make scripts executable
chmod +x install-android.sh start-android.sh
```

### Runtime Issues

**Web UI won't load**
- Check if port 50001 is available
- Try a different port by editing run_ui.py
- Check logs for errors

**Models not responding**
- Verify API keys in .env
- Check internet connection
- Try a different model provider
- Ensure you have credits/quota on your API provider

**Out of memory**
- Close other apps
- Use smaller models (e.g., gpt-3.5-turbo instead of gpt-4)
- Restart Termux

### Performance Tips

1. **Use smaller models** for better performance on mobile
2. **Close background apps** to free up memory
3. **Use Wi-Fi** for better internet connectivity
4. **Consider local models** via Ollama (slower but no API costs)

## Advanced Configuration

### Custom Models

You can add custom models by editing `conf/model_providers.yaml`:

```yaml
custom_provider:
  api_base: "http://localhost:8000/v1"
  models:
    - name: "my-custom-model"
      input_cost: 0.001
      output_cost: 0.002
```

### Memory Management

Agent Zero stores memories in the `memory/android/` directory:
- Save memory for context across sessions
- Load memory for relevant context
- Delete memories when no longer needed

### Knowledge Base

Add documents to `knowledge/android/` for the agent to reference:
- Text files (.txt, .md)
- The agent can query and analyze these files

## Security Considerations

⚠️ **Important Security Notes:**

1. **API Keys** - Keep your .env file secure and never share it
2. **Network Access** - The web UI is accessible to any device on your network
3. **Code Execution** - The agent can execute potentially dangerous code - use carefully
4. **Updates** - Regularly update Agent Zero for security patches

### Recommended Security Practices

- Use a strong MCP_SERVER_TOKEN (random string)
- Don't expose the web UI to the public internet without protection
- Regularly rotate API keys
- Use a dedicated API key for Agent Zero

## Development & Customization

### Creating Custom Tools

Tools are located in `python/tools/`. Each tool is a Python class that inherits from `Tool`.

Example tool structure:
```python
from python.helpers.tool import Tool, Response

class MyCustomTool(Tool):
    async def execute(self, **kwargs):
        # Your tool logic here
        return Response(message="Tool executed!", break_loop=False)

    def get_heading(self):
        return "My Custom Tool"
```

### Customizing Prompts

System prompts are in the `prompts/` directory:
- `prompts/default/agent.system.md` - Main agent behavior
- Modify to change how the agent thinks and acts

### Extensions

Agent Zero has an extension system in `python/extensions/`:
- Message loop hooks
- Tool execution hooks
- Response streaming hooks

## Getting Help

- **GitHub Issues**: https://github.com/JDAWosx/agent-zero/issues
- **Official Documentation**: https://agent-zero.ai
- **Discord Community**: https://discord.gg/B8KZKNsPpj
- **Termux Wiki**: https://wiki.termux.com

## What's Different from the Original

This Android/Termux version modifies the original Agent Zero:

1. **Tool Filtering** - Browser automation tools excluded
2. **Docker Handling** - Gracefully degrades to local shell
3. **Dependency Management** - Conditional imports for incompatible packages
4. **Android Detection** - Automatic platform detection
5. **Simplified Setup** - Streamlined installation for Termux

## Credits

- **Original Project**: Agent Zero by agent0ai
- **GitHub**: https://github.com/agent0ai/agent-zero
- **Website**: https://agent-zero.ai
- **Android Port**: Modified by @JDAWosx

## License

This project inherits the original Agent Zero license. See the LICENSE file for details.

## Changelog

### Android Port Changes

- Added Android/Termux detection and compatibility layer
- Filtered out browser automation tools for Android
- Modified Docker handling for non-Docker environments
- Created simplified installation scripts
- Added Android-specific documentation
- Conditional dependency loading

---

**Enjoy Agent Zero on your Android device!** 🤖📱
