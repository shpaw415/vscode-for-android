#!/bin/bash

if [ -f ./inited ]; then
    echo "Starting VS Code Server..."
    code-server --bind-addr 127.0.0.1:8080 --auth none --disable-telemetry
else
    curl -fsSL https://code-server.dev/install.sh | sh
    touch ./inited
    echo "VS Code Server installed."
fi

