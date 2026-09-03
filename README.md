# InvestFlow — Investment & Portfolio Management Platform

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.x-blue.svg)](https://spring.io/projects/spring-cloud)
[![SQL Server](https://img.shields.io/badge/Database-SQL%20Server%202022-red.svg)](https://www.microsoft.com/sql-server)
[![Redis](https://img.shields.io/badge/Cache-Redis%207-critical.svg)](https://redis.io/)
[![Python](https://img.shields.io/badge/FastAPI-Python%203.11-yellow.svg)](https://fastapi.tiangolo.com/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

> **InvestFlow** is an enterprise-grade FinTech distributed platform designed to demonstrate modern backend engineering capabilities expected of a 4+ year Java/Spring Boot senior engineer. The platform powers portfolio analytics, multi-asset order execution, XIRR calculations via an optimized Python engine, real-time streaming via SSE & WebSockets, and an AI Natural-Language-to-SQL financial assistant.

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
         ┌──────────────┬──────────────┬───────────────┬──────────────┬──────────────┐
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

| Service | Technology | Port | Responsibilities |
|---|---|---|---|
| **api-gateway** | Spring Cloud Gateway | `8080` | Unified API routing, rate limiting, request correlation IDs |
| **user-service** | Spring Boot 3.5, Security | `8081` | Registration, login, BCrypt, JWT tokens, RBAC (`USER`, `ADMIN`) |
| **portfolio-service** | Spring Boot 3.5, JPA | `8082` | Portfolio CRUD, holdings management, asset allocation breakdowns |
| **investment-service**| Spring Boot 3.5, JPA | `8083` | Stocks, Mutual Funds, SIP schedules, buy/sell transactional integrity |
| **analytics-service** | Spring Boot 3.5, SSE, Redis | `8084` | P&L, returns, portfolio X-ray, cash flow aggregation, SSE real-time feed |
| **notification-service**| Spring Boot 3.5, WebSocket | `8085` | Real-time WebSocket push notifications and portfolio event alerts |
| **ai-service** | Spring Boot 3.5, LLM API | `8086` | Natural-language query translation into safe, read-only SQL queries |
| **xirr-service** | Python 3.11, FastAPI, SciPy| `8005` | High-precision Newton-Raphson cash-flow XIRR calculation engine |
| **angular-app** | Angular 19 SPA | `4200` | Responsive financial dashboard, interactive charts, real-time widgets |

---

## 🛠️ Technology Stack

- **Core Backend**: Java 21 LTS, Spring Boot 3.5.x, Spring Web, Spring Data JPA, Hibernate, Bean Validation
- **Security**: Spring Security 6, JJWT (HMAC-SHA256, Refresh Tokens, RBAC)
- **Databases & Cache**: Microsoft SQL Server 2022 (Flyway migrations), Redis 7 (Cache-Aside, TTL)
- **Communication Protocols**: RESTful APIs, WebSockets (STOMP), Server-Sent Events (SSE)
- **Specialized Engine**: Python 3.11, FastAPI, NumPy, SciPy (Optimization root-finding)
- **Frontend**: Angular 19, TypeScript, RxJS, Reactive Forms
- **Infrastructure**: Docker, Docker Compose, NGINX, GitHub Actions, AWS EC2 ready

---

## 🚀 Phase 1: Local Development Setup

### Prerequisites
- **Java 21 LTS** installed (`java -version`)
- **Maven 3.9+** installed (`mvn -version`)
- **Docker & Docker Compose** running (`docker compose version`)

### 1. Configure Environment
```bash
cp .env.example .env
```

### 2. Launch SQL Server & Redis Infrastructure
```bash
docker compose up -d sqlserver redis
```

Verify that the containers are healthy:
```bash
docker compose ps
```

### 3. Initialize Isolated Databases
Run the initialization script against SQL Server to provision isolated databases for all microservices:
```bash
docker compose exec -T sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "InvestFlow@2026!Secure" -C \
  -i /init-scripts/init-databases.sql
```

### 4. Build All Microservices
Run the multi-module Maven compilation from the root repository:
```bash
mvn clean compile
```

Each microservice can also be compiled and executed independently:
```bash
cd services/user-service && mvn clean compile
```

---

## 📂 Monorepo Structure

```
investflow/
├── README.md                          # Master documentation
├── docker-compose.yml                 # Multi-container orchestration
├── .gitignore                         # Comprehensive Git ignore rules
├── .env.example                       # Environment configuration template
├── pom.xml                            # Root aggregator & dependency management
│
├── services/
│   ├── api-gateway/                   # Spring Cloud Gateway
│   ├── user-service/                  # Auth & User Service
│   ├── portfolio-service/             # Portfolio & Holdings
│   ├── investment-service/            # Investment & SIP Service
│   ├── analytics-service/             # Analytics & SSE Service
│   ├── notification-service/          # WebSocket Notification Service
│   ├── ai-service/                    # NL-to-SQL AI Service
│   └── xirr-service/                  # Python FastAPI Calculation Engine
│
├── frontend/
│   └── angular-app/                   # Angular SPA
│
├── infrastructure/
│   ├── nginx/                         # NGINX reverse proxy configuration
│   ├── docker/                        # Multi-stage Dockerfiles
│   └── aws/                           # Cloud deployment resources
│
├── database/
│   ├── migrations/                    # Flyway migration scripts
│   └── scripts/                       # Database bootstrap scripts
│
├── docs/
│   ├── architecture/                  # Architecture specifications & diagrams
│   ├── api/                           # API documentation & schemas
│   ├── database/                      # Schema design & ER diagrams
│   └── deployment/                    # Production deployment guides
│
├── postman/                           # Postman collections
└── .github/
    └── workflows/                     # GitHub Actions CI/CD pipeline
```

---

## 🔒 Security Principles
- Passwords hashed using **BCrypt** with salt factor 12.
- Short-lived JWT access tokens and long-lived refresh tokens.
- Strict database isolation between microservices.
- Safe AST validation on AI queries preventing any destructive operations.
- Zero hardcoded credentials; strict environment variable externalization.
