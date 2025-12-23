#!/data/data/com.termux/files/usr/bin/bash

# Auto-install and launch code-server script
# This script will be placed in ~/.termux/ directory

echo "================================"
echo "VS Code Server Setup"
echo "================================"
echo ""




if [ -f ./inited ]; then
    echo "[Termux] Ready!"
else
    echo "Installing Ubuntu..."
    echo "This will take several minutes. Please wait..."
    echo ""
    yes | pkg update
    yes | pkg install proot-distro
    proot-distro install ubuntu
    yes | pkg upgrade
    proot-distro copy ./run.sh ubuntu:/root/run.sh
    proot-distro login ubuntu -- bash /root/run.sh
    touch inited
    clear
fi

echo ""
echo "================================"
echo "Opening VS Code in browser..."
echo "================================"
echo ""
echo "URL: http://127.0.0.1:8080"
echo ""


nohup proot-distro login ubuntu -- bash /root/run.sh >/dev/null 2>&1 &

echo "Waiting for VS Code Server to start..."
sleep 10

# Send intent to open CodeServerActivity
am start -n com.termux/.app.activities.CodeServerActivity

echo "Done! VS Code should open in WebView."
