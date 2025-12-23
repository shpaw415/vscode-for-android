#!/data/data/com.termux/files/usr/bin/bash

# Auto-install and launch code-server script
# This script will be placed in ~/.termux/ directory

echo "================================"
echo "VS Code Server Setup"
echo "================================"
echo ""


echo "Installing Ubuntu..."
echo "This will take several minutes. Please wait..."
echo ""

yes | pkg update
yes | pkg install tur-repo
yes | pkg install code-server
yes | pkg upgrade
nohup code-server --bind-addr 127.0.0.1:8080 --auth none --disable-telemetry > ~/.code-server.log 2>&1 &

clear

echo ""
echo "================================"
echo "Opening VS Code in browser..."
echo "================================"
echo ""
echo "URL: http://127.0.0.1:8080"
echo ""

sleep 5

am start -n com.termux/.app.activities.CodeServerActivity