#!/data/data/com.termux/files/usr/bin/bash

# Auto-install and launch code-server script
# This script will be placed in ~/.termux/ directory

echo "================================"
echo "VS Code Server Setup"
echo "================================"
echo ""

# Check if code-server is already installed
if command -v proot-distro &> /dev/null; then
    echo "✓ ubuntu is already installed"
else
    echo "Installing Ubuntu..."
    echo "This will take several minutes. Please wait..."
    echo ""
    
    # Install code-server with automatic yes to all prompts
    # Important: Install tur-repo and code-server FIRST, then update
    pkg update -y
    yes | pkg install proot-distro -y
    proot-distro install ubuntu && proot-distro login ubuntu -- curl -fsSL https://code-server.dev/install.sh | sh
    yes | pkg upgrade -y
    echo "Starting code-server..."
    proot-distro login ubuntu -- nohup code-server --bind-addr 127.0.0.1:8080 --auth none --disable-telemetry > ~/.code-server.log 2>&1 &
fi

echo ""
echo "================================"
echo "Opening VS Code in browser..."
echo "================================"
echo ""
echo "URL: http://127.0.0.1:8080"
echo ""

# Send intent to open CodeServerActivity
am start -n com.termux/.app.activities.CodeServerActivity

echo "Done! VS Code should open in WebView."
