#!/bin/bash
# ==============================================================================
# InvestFlow Master Orchestration Script - Stop All Services
# ==============================================================================

BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="$BASE_DIR/scripts/.pids"

echo "Stopping all InvestFlow services..."

if [ -f "$PID_FILE" ]; then
    while IFS= read -r pid; do
        if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then
            echo "Terminating PID: $pid"
            kill "$pid" 2>/dev/null || true
        fi
    done < "$PID_FILE"
    rm -f "$PID_FILE"
fi

# Kill any stray processes by port if still active
for port in 4200 8080 8081 8082 8083 8084 8085 8086 8005; do
    pid=$(lsof -ti :$port || true)
    if [ -n "$pid" ]; then
        echo "Releasing port $port (PID: $pid)..."
        kill -9 $pid 2>/dev/null || true
    fi
done

echo "All InvestFlow services stopped successfully."
