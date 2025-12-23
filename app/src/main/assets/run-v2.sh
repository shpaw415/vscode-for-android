nohup code-server --bind-addr 127.0.0.1:8080 --auth none --disable-telemetry > ~/.code-server.log 2>&1 &


echo ""
echo "================================"
echo "Opening VS Code in browser..."
echo "================================"
echo ""
echo "URL: http://127.0.0.1:8080"
echo ""

sleep 5

am start -n com.termux/.app.activities.CodeServerActivity