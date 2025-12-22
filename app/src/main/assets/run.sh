#!/data/data/com.termux/files/usr/bin/bash


proot-distro login ubuntu -- nohup code-server --bind-addr 127.0.0.1:8080 --auth none --disable-telemetry > ~/.code-server.log 2>&1 &
sleep 3
am start -n com.termux/.app.activities.CodeServerActivity