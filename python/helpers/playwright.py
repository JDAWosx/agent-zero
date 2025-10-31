from pathlib import Path
import subprocess
import os
from python.helpers import files
from python.helpers.print_style import PrintStyle
from python.helpers.android_compat import is_android


# this helper ensures that playwright is installed in /lib/playwright
# should work for both docker and local installation

def get_playwright_binary():
    """Get Playwright chromium headless shell binary"""
    pw_cache = Path(get_playwright_cache_dir())
    headless_shell = next(pw_cache.glob("chromium_headless_shell-*/chrome-*/headless_shell"), None)

    # If not found, try regular chromium
    if not headless_shell:
        headless_shell = next(pw_cache.glob("chromium-*/chrome-linux/chrome"), None)

    # If still not found, check for system chromium on Android
    if not headless_shell and is_android():
        # Check if Chromium is installed via Termux
        chromium_path = Path("/data/data/com.termux/files/usr/bin/chromium")
        if chromium_path.exists():
            return chromium_path

    return headless_shell

def get_playwright_cache_dir():
    return files.get_abs_path("tmp/playwright")

def ensure_playwright_binary():
    """Ensure Playwright binary is available"""
    bin = get_playwright_binary()

    if not bin:
        PrintStyle.hint("Installing Playwright browser binaries...")
        cache = get_playwright_cache_dir()
        env = os.environ.copy()
        env["PLAYWRIGHT_BROWSERS_PATH"] = cache

        try:
            # Try to install chromium headless shell
            subprocess.check_call(
                ["playwright", "install", "chromium", "--only-shell"],
                env=env,
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL
            )
        except Exception as e:
            PrintStyle.warning(f"Failed to install Playwright headless shell: {e}")
            try:
                # Fallback: install full chromium
                subprocess.check_call(
                    ["playwright", "install", "chromium"],
                    env=env,
                    stdout=subprocess.DEVNULL,
                    stderr=subprocess.DEVNULL
                )
                PrintStyle.hint("Installed full Chromium instead of headless shell")
            except Exception as e2:
                PrintStyle.warning(f"Failed to install Chromium: {e2}")
                # Last resort: use system chromium if available
                if is_android():
                    chromium_path = Path("/data/data/com.termux/files/usr/bin/chromium")
                    if chromium_path.exists():
                        PrintStyle.hint(f"Using system Chromium: {chromium_path}")
                        return str(chromium_path)
                raise Exception(
                    "Playwright browser not available. "
                    "On Android, try: pkg install chromium"
                )

    bin = get_playwright_binary()
    if not bin:
        raise Exception(
            "Playwright binary not found after installation. "
            "Please install manually or use a system browser."
        )
    return bin
