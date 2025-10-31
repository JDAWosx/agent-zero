"""
Android/Termux Compatibility Module

This module provides compatibility checks and utilities for running Agent Zero on Android/Termux.
"""
import os
import sys

def is_android() -> bool:
    """Check if running on Android via Termux"""
    return (
        os.path.exists("/data/data/com.termux")
        or os.environ.get("TERMUX_VERSION") is not None
        or "com.termux" in os.environ.get("PREFIX", "")
    )

def is_docker_available() -> bool:
    """Check if Docker is available (it won't be on Android)"""
    if is_android():
        return False

    try:
        import docker
        docker.from_env()
        return True
    except Exception:
        return False

def is_playwright_available() -> bool:
    """Check if Playwright and browser automation are available"""
    if is_android():
        return False

    try:
        import playwright
        return True
    except ImportError:
        return False

def is_browser_use_available() -> bool:
    """Check if browser-use package is available"""
    if is_android():
        return False

    try:
        import browser_use
        return True
    except ImportError:
        return False

def is_faiss_available() -> bool:
    """Check if FAISS for vector search is available"""
    try:
        import faiss
        return True
    except ImportError:
        return False

def get_excluded_tools() -> list[str]:
    """
    Return list of tool names to exclude on Android.
    These are tools that require dependencies not available or suitable for Android.
    """
    excluded = []

    # Docker tools are handled gracefully, no need to exclude
    # Browser automation can work if dependencies are installed

    # Only exclude if Playwright is definitely not available
    if not is_playwright_available() and not is_browser_use_available():
        # Only exclude if we're sure browser automation won't work
        pass

    return excluded

def filter_tools_for_platform(tool_names: list[str]) -> list[str]:
    """
    Filter tool names based on platform compatibility.
    Returns only tools that are compatible with the current platform.
    """
    excluded = get_excluded_tools()
    return [name for name in tool_names if name not in excluded]
