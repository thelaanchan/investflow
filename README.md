# InvestFlow — FinTech Investment & Portfolio Management Platform

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.x-blue.svg)](https://spring.io/projects/spring-cloud)
[![Angular](https://img.shields.io/badge/Angular-19-red.svg)](https://angular.dev/)
[![SQL Server](https://img.shields.io/badge/Database-SQL%20Server%202022-red.svg)](https://www.microsoft.com/sql-server)
[![Redis](https://img.shields.io/badge/Cache-Redis%207-critical.svg)](https://redis.io/)
[![Python](https://img.shields.io/badge/FastAPI-Python%203.9%2B-yellow.svg)](https://fastapi.tiangolo.com/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

> **InvestFlow** is an institutional-grade, production-style FinTech distributed investment platform designed to demonstrate modern backend engineering capabilities. The platform powers real-time portfolio tracking, multi-asset order execution, XIRR calculations via an optimized Python SciPy engine, live valuation streaming via Server-Sent Events (SSE) and WebSockets, and an AI Natural-Language-to-SQL financial assistant with AST security validation.

---

## 🏛️ System Architecture

```
                                      [ Internet / Browser Client ]
                                                   │
                                                   ▼
                                      [ NGINX Reverse Proxy :80 ]
                                                   │
                                                   ▼
                                     [ Spring Cloud Gateway :8080 ]
                                                   │
             ┌──────────────┬──────────────┬───────┴───────┬──────────────┬──────────────┐
             ▼              ▼              ▼               ▼              ▼              ▼
       [ User Svc ]  [ Port Svc ]   [ Inv Svc ]    [ Analytics Svc ] [ Notif Svc ]   [ AI Svc ]
          :8081          :8082          :8083            :8084           :8085          :8086
             │              │              │               │              │              │
             └──────────────┴──────────────┼───────────────┴──────────────┴──────────────┘
                                           ▼
                           [ Microsoft SQL Server 2022 ]
                      (Dedicated Isolated DBs per Microservice)
                                           │
                                           ▼
                                 [ Redis 7 Cluster ]
                              (Cache-Aside, Rate Limits)
                                           │
                            (Analytics -> Python XIRR :8005)
```

---

## 📦 Microservices Breakdown

| Service | Technology Stack | Port | Responsibilities |
|---|---|---|---|
| **api-gateway** | Spring Cloud Gateway, WebFlux | `8080` | Unified API routing, CORS management, correlation ID propagation |
| **user-service** | Spring Boot 3.5, Spring Security 6, JJWT | `8081` | Registration, login, BCrypt, JWT tokens, RBAC (`ROLE_USER`, `ROLE_ADMIN`) |
| **portfolio-service** | Spring Boot 3.5, Spring Data JPA, Flyway | `8082` | Portfolio CRUD, holdings management, asset allocation breakdowns |
| **investment-service**| Spring Boot 3.5, Spring Data JPA, Flyway | `8083` | Stocks, Mutual Funds, SIP schedules, buy/sell transactional integrity |
| **analytics-service** | Spring Boot 3.5, CompletableFuture, SSE, Redis | `8084` | Multithreaded returns calculation, XIRR bridge, live SSE valuation ticker |
| **notification-service**| Spring Boot 3.5, Spring WebSocket, STOMP | `8085` | Real-time WebSocket push notifications and portfolio event alerts |
| **ai-service** | Spring Boot 3.5, AST Validation, JdbcTemplate | `8086` | Natural-language query translation into safe, read-only SQL queries |
| **xirr-service** | Python 3.9+, FastAPI, NumPy, SciPy | `8005` | High-precision Newton-Raphson cash-flow XIRR calculation engine |
| **angular-app** | Angular 19 SPA, TypeScript, RxJS | `4200` | Responsive financial dashboard, SVG charts, live ticker, AI copilot |

---

## 🔑 Demo Credentials

| Role | Email | Password | Permissions |
|---|---|---|---|
| **Demo User** | `user@investflow.com` | `User@12345` | Full portfolio, trading, SIP, analytics & AI copilot access |
| **Platform Admin** | `admin@investflow.com` | `Admin@12345` | System telemetry, all microservices, and user management |

---

## 🚀 Quick Start: Running All Applications

### Prerequisites
- **Java 21 LTS** installed (`export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"`)
- **Docker & Docker Compose** installed and running
- **Node.js (v18+)** and **npm** installed
- **Python 3.9+** installed

---

### Option A: One-Click Orchestration (Recommended)

The repository includes pre-configured orchestration scripts to start and stop all 10 applications with a single command.

#### 1. Start All Applications
From the project root:
```bash
./scripts/run-all.sh
```

This script will automatically:
1. Verify and start Docker containers (**SQL Server 2022** on `1433` and **Redis 7** on `6379`).
2. Start the **Python XIRR FastAPI service** on `http://localhost:8005`.
3. Start all 7 Spring Boot microservices (**User**, **Portfolio**, **Investment**, **Analytics**, **Notification**, **AI**, **Gateway**).
4. Start the **Angular 19 Frontend** on `http://localhost:4200`.
5. Write all process IDs to `scripts/.pids` and log streams to `logs/*.log`.

#### 2. Access the Platform
Open your browser and navigate to:
- **Angular Frontend UI**: [http://localhost:4200](http://localhost:4200)
- **API Gateway**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI (Interactive API Docs)**:
  - User Service: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
  - Portfolio Service: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
  - Investment Service: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
  - Analytics Service: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)
  - Notification Service: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
  - AI Copilot Service: [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html)
  - Python XIRR Docs: [http://localhost:8005/docs](http://localhost:8005/docs)

#### 3. Stop All Applications
To cleanly terminate all running background processes:
```bash
./scripts/stop-all.sh
```

---

### Option B: Manual Step-by-Step Execution

If you prefer to launch each component individually in separate terminals:

#### Step 1: Start Database & Cache Containers
```bash
docker compose up -d
```
Verify that SQL Server (`1433`) and Redis (`6379`) are running:
```bash
docker compose ps
```

#### Step 2: Initialize Database Schemas (First Time Only)
```bash
docker compose exec -T sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "InvestFlow@2026!Secure" -C \
  -i /init-scripts/init-databases.sql
```

#### Step 3: Start Python XIRR Calculation Engine
```bash
cd services/xirr-service
python3 -m venv venv
./venv/bin/pip install -r requirements.txt
./venv/bin/uvicorn main:app --host 0.0.0.0 --port 8005
```

#### Step 4: Build Backend Microservices
From the project root:
```bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
./mvnw clean package -DskipTests
```

#### Step 5: Launch Each Spring Boot Microservice
Open separate terminal tabs or run in background:

```bash
# Terminal 1: User Service (Port 8081)
cd services/user-service && java -jar target/user-service-1.0.0-SNAPSHOT.jar

# Terminal 2: Portfolio Service (Port 8082)
cd services/portfolio-service && java -jar target/portfolio-service-1.0.0-SNAPSHOT.jar

# Terminal 3: Investment Service (Port 8083)
cd services/investment-service && java -jar target/investment-service-1.0.0-SNAPSHOT.jar

# Terminal 4: Analytics Service (Port 8084)
cd services/analytics-service && java -jar target/analytics-service-1.0.0-SNAPSHOT.jar

# Terminal 5: Notification Service (Port 8085)
cd services/notification-service && java -jar target/notification-service-1.0.0-SNAPSHOT.jar

# Terminal 6: AI NL-to-SQL Service (Port 8086)
cd services/ai-service && java -jar target/ai-service-1.0.0-SNAPSHOT.jar

# Terminal 7: API Gateway (Port 8080)
cd services/api-gateway && java -jar target/api-gateway-1.0.0-SNAPSHOT.jar
```

#### Step 6: Start Angular Frontend Application
```bash
cd frontend/angular-app
npm install
npm start
```
The application will be live at `http://localhost:4200`.

---

## 🧪 Running Automated Unit & Integration Tests

To run the complete automated test suite across all 8 Maven modules:
```bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
./mvnw test
```

To run tests for an individual microservice:
```bash
cd services/user-service && ../../mvnw test
cd services/portfolio-service && ../../mvnw test
cd services/investment-service && ../../mvnw test
cd services/analytics-service && ../../mvnw test
cd services/notification-service && ../../mvnw test
cd services/ai-service && ../../mvnw test
```

---

## 📡 API Verification via cURL

### 1. Authenticate & Retrieve JWT Token
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@investflow.com","password":"User@12345"}' | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
```

### 2. Fetch User Portfolios
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/portfolios
```

### 3. Fetch Active Holdings & Positions
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/investments
```

### 4. Execute a BUY Trade Order
```bash
curl -X POST http://localhost:8080/api/investments/1/buy \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"units": 5.0, "price": 228.40}'
```

### 5. Fetch Portfolio Analytics & XIRR
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/analytics/portfolio/1
```

### 6. Query AI Financial Assistant (NL-to-SQL)
```bash
curl -X POST http://localhost:8080/api/ai/query \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"question": "What is my total investment and portfolio value?"}'
```

### 7. Subscribe to Real-Time SSE Valuation Stream
```bash
curl -N http://localhost:8080/api/analytics/portfolio/1/events
```

---

## 🔒 Security & Architecture Guardrails

- **Stateless Authentication**: JJWT with HMAC-SHA384 signed tokens, 24-hour expiration, and refresh token rotation.
- **Microservice Database Isolation**: Distinct isolated databases (`investflow_user`, `investflow_portfolio`, etc.) with Read Committed Snapshot Isolation (RCSI).
- **Concurrency & Parallelism**: Java 21 `CompletableFuture` executes parallel quantitative analytics and benchmark calculations.
- **Cache-Aside Pattern**: High-frequency portfolio metrics and allocation breakdowns cached in Redis 7 with automatic invalidation on trades.
- **AI AST Security**: Custom `SqlSafetyValidator` enforces read-only `SELECT` queries, rejects statement chaining (`;`), strips comments (`--`, `/*`), and injects tenant-isolation predicates (`WHERE user_id = ?`).

---

## 📄 License
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
