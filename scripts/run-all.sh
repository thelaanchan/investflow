#!/bin/bash
# ==============================================================================
# InvestFlow Master Orchestration Script - Start All Services
# ==============================================================================

set -e

BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$BASE_DIR/logs"
PID_FILE="$BASE_DIR/scripts/.pids"

mkdir -p "$LOG_DIR"
rm -f "$PID_FILE"
touch "$PID_FILE"

export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "=================================================================="
echo "          INVESTFLOW — LAUNCHING MICROSERVICES & APPS             "
echo "=================================================================="

# 1. Check & Start Docker Infrastructure
echo "[1/10] Verifying Docker Containers (SQL Server 2022 & Redis 7)..."
cd "$BASE_DIR"
if ! docker ps | grep -q "investflow-sqlserver"; then
    echo "Starting Docker Compose dependencies..."
    docker compose up -d
    echo "Waiting for SQL Server to accept connections..."
    sleep 8
else
    echo "Docker containers are healthy and running."
fi

# 2. Start Python XIRR Engine
echo "[2/10] Starting Python XIRR FastAPI Engine (Port 8005)..."
cd "$BASE_DIR/services/xirr-service"
"$BASE_DIR/services/xirr-service/venv/bin/uvicorn" main:app --host 0.0.0.0 --port 8005 > "$LOG_DIR/xirr-service.log" 2>&1 &
echo $! >> "$PID_FILE"

# 3. Start User Service
echo "[3/10] Starting User Service (Port 8081)..."
cd "$BASE_DIR/services/user-service"
java -jar target/user-service-1.0.0-SNAPSHOT.jar > "$LOG_DIR/user-service.log" 2>&1 &
echo $! >> "$PID_FILE"

# 4. Start Portfolio Service
echo "[4/10] Starting Portfolio Service (Port 8082)..."
cd "$BASE_DIR/services/portfolio-service"
java -jar target/portfolio-service-1.0.0-SNAPSHOT.jar > "$LOG_DIR/portfolio-service.log" 2>&1 &
echo $! >> "$PID_FILE"

# 5. Start Investment Service
echo "[5/10] Starting Investment & SIP Service (Port 8083)..."
cd "$BASE_DIR/services/investment-service"
java -jar target/investment-service-1.0.0-SNAPSHOT.jar > "$LOG_DIR/investment-service.log" 2>&1 &
echo $! >> "$PID_FILE"

# 6. Start Analytics Service
echo "[6/10] Starting Analytics Service (Port 8084)..."
cd "$BASE_DIR/services/analytics-service"
java -jar target/analytics-service-1.0.0-SNAPSHOT.jar > "$LOG_DIR/analytics-service.log" 2>&1 &
echo $! >> "$PID_FILE"

# 7. Start Notification Service
echo "[7/10] Starting Notification & WebSocket Service (Port 8085)..."
cd "$BASE_DIR/services/notification-service"
java -jar target/notification-service-1.0.0-SNAPSHOT.jar > "$LOG_DIR/notification-service.log" 2>&1 &
echo $! >> "$PID_FILE"

# 8. Start AI Service
echo "[8/10] Starting AI Financial Assistant Service (Port 8086)..."
cd "$BASE_DIR/services/ai-service"
java -jar target/ai-service-1.0.0-SNAPSHOT.jar > "$LOG_DIR/ai-service.log" 2>&1 &
echo $! >> "$PID_FILE"

# 9. Start API Gateway
echo "[9/10] Starting API Gateway (Port 8080)..."
cd "$BASE_DIR/services/api-gateway"
java -jar target/api-gateway-1.0.0-SNAPSHOT.jar > "$LOG_DIR/api-gateway.log" 2>&1 &
echo $! >> "$PID_FILE"

# 10. Start Angular Frontend
echo "[10/10] Starting Angular 19 Frontend (Port 4200)..."
cd "$BASE_DIR/frontend/angular-app"
npx ng serve --host 0.0.0.0 --port 4200 > "$LOG_DIR/frontend.log" 2>&1 &
echo $! >> "$PID_FILE"

echo ""
echo "=================================================================="
echo "           ALL INVESTFLOW APPS SUCCESSFULLY LAUNCHED!             "
echo "=================================================================="
echo "  • Angular Web Application:     http://localhost:4200"
echo "  • API Gateway:                 http://localhost:8080"
echo "  • User Service:                http://localhost:8081/swagger-ui.html"
echo "  • Portfolio Service:           http://localhost:8082/swagger-ui.html"
echo "  • Investment Service:          http://localhost:8083/swagger-ui.html"
echo "  • Analytics Service:           http://localhost:8084/swagger-ui.html"
echo "  • Notification Service:        http://localhost:8085/swagger-ui.html"
echo "  • AI NL-to-SQL Service:        http://localhost:8086/swagger-ui.html"
echo "  • Python XIRR Engine:          http://localhost:8005/docs"
echo "  • SQL Server 2022:             localhost:1433"
echo "  • Redis 7 Alpine:              localhost:6379"
echo "=================================================================="
echo "  Demo Credentials:"
echo "    - User:  user@investflow.com  /  User@12345"
echo "    - Admin: admin@investflow.com /  Admin@12345"
echo "=================================================================="
echo "  Logs are streaming to: $LOG_DIR/*.log"
echo "  To stop all services, run: ./scripts/stop-all.sh"
echo "=================================================================="
