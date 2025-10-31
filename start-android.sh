#!/bin/bash
# Agent Zero Android/Termux Startup Script

echo "=========================================="
echo "Agent Zero for Android/Termux"
echo "=========================================="
echo ""

# Check if running in Termux
if [ ! -d "$PREFIX" ]; then
    echo "Error: This script is designed for Termux on Android"
    exit 1
fi

# Check if .env exists
if [ ! -f ".env" ]; then
    echo "Warning: .env file not found!"
    echo "Creating a template .env file..."
    echo ""
    echo "Please edit the .env file with your API keys before running again."
    echo "nano .env"
    echo ""
    exit 1
fi

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}Starting Agent Zero...${NC}"
echo ""
echo "Agent Zero will be available at:"
echo "  http://localhost:50001"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop${NC}"
echo ""

# Start the web UI
python run_ui.py
