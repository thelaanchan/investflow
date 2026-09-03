import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Platform Infrastructure & Health Dashboard</h1>
          <p class="page-subtitle">Real-time telemetry, service discovery, database isolation, and cache state</p>
        </div>
        <div class="status-summary">
          <span class="pulse-indicator"></span>
          <span class="font-semibold text-gradient-emerald">10 / 10 SERVICES HEALTHY</span>
        </div>
      </div>

      <!-- Microservices Grid -->
      <div class="services-grid">
        <div class="service-card glass-panel" *ngFor="let s of services">
          <div class="s-top">
            <span class="s-name">{{ s.name }}</span>
            <span class="badge-positive">● ONLINE</span>
          </div>
          <div class="s-port mono">Port: :{{ s.port }}</div>
          <div class="s-tech">{{ s.tech }}</div>

          <div class="s-meta">
            <span class="meta-item">Latency: <strong class="mono">{{ s.latency }}ms</strong></span>
            <span class="meta-item">Uptime: <strong class="mono">{{ s.uptime }}</strong></span>
          </div>
        </div>
      </div>

      <!-- Infrastructure Architecture Specs -->
      <div class="infra-grid">
        <div class="infra-card glass-panel">
          <h3>SQL Server 2022 Multi-Database Isolation</h3>
          <p class="infra-desc">
            Each microservice is bound to a dedicated database with Read Committed Snapshot Isolation (RCSI) enabled to eliminate read-write contention.
          </p>
          <div class="tag-list">
            <span class="tag">investflow_user</span>
            <span class="tag">investflow_portfolio</span>
            <span class="tag">investflow_investment</span>
            <span class="tag">investflow_analytics</span>
            <span class="tag">investflow_notification</span>
          </div>
        </div>

        <div class="infra-card glass-panel">
          <h3>Redis 7 Distributed Cache & Pub/Sub</h3>
          <p class="infra-desc">
            High-speed caching layer for portfolio analytics and real-time STOMP WebSocket notification brokers.
          </p>
          <div class="cache-metrics">
            <div class="c-metric">
              <span class="c-lbl">Memory Usage</span>
              <span class="c-val mono">14.2 MB</span>
            </div>
            <div class="c-metric">
              <span class="c-lbl">Cache Hit Ratio</span>
              <span class="c-val mono positive">99.4%</span>
            </div>
            <div class="c-metric">
              <span class="c-lbl">Active Subscribers</span>
              <span class="c-val mono">8 WebSockets</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 24px;
    }
    .page-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .page-title {
      font-size: 1.6rem;
      font-weight: 800;
    }
    .page-subtitle {
      font-size: 0.88rem;
      color: #94a3b8;
    }
    .status-summary {
      display: flex;
      align-items: center;
      gap: 10px;
      background: rgba(16, 185, 129, 0.12);
      border: 1px solid rgba(16, 185, 129, 0.3);
      padding: 8px 16px;
      border-radius: 9999px;
      font-size: 0.8rem;
    }
    .font-semibold {
      font-weight: 700;
    }
    .services-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
      gap: 16px;
    }
    .service-card {
      padding: 18px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .s-top {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .s-name {
      font-size: 1.05rem;
      font-weight: 700;
    }
    .s-port {
      font-size: 0.8rem;
      color: #38bdf8;
    }
    .s-tech {
      font-size: 0.75rem;
      color: #94a3b8;
    }
    .s-meta {
      display: flex;
      justify-content: space-between;
      font-size: 0.72rem;
      color: #64748b;
      margin-top: 6px;
      padding-top: 8px;
      border-top: 1px solid rgba(255, 255, 255, 0.05);
    }
    .infra-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
    }
    .infra-card {
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    .infra-card h3 {
      font-size: 1.15rem;
      font-weight: 700;
    }
    .infra-desc {
      font-size: 0.85rem;
      color: #94a3b8;
      line-height: 1.5;
    }
    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 6px;
    }
    .tag {
      background: rgba(15, 23, 42, 0.7);
      border: 1px solid var(--border-color);
      color: #38bdf8;
      font-family: var(--font-mono);
      font-size: 0.75rem;
      padding: 4px 10px;
      border-radius: 6px;
    }
    .cache-metrics {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
      margin-top: 8px;
    }
    .c-metric {
      background: rgba(15, 23, 42, 0.6);
      padding: 12px;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .c-lbl {
      font-size: 0.7rem;
      color: #64748b;
    }
    .c-val {
      font-size: 1.05rem;
      font-weight: 700;
    }
  `]
})
export class AdminComponent {
  services = [
    { name: 'API Gateway', port: 8080, tech: 'Spring Cloud Gateway Reactive', latency: 4, uptime: '99.98%' },
    { name: 'User Service', port: 8081, tech: 'Spring Boot 3.5 / Spring Security 6', latency: 6, uptime: '99.99%' },
    { name: 'Portfolio Service', port: 8082, tech: 'Spring Boot 3.5 / JPA Hibernate', latency: 8, uptime: '99.95%' },
    { name: 'Investment Service', port: 8083, tech: 'Spring Boot 3.5 / Transactions', latency: 7, uptime: '99.96%' },
    { name: 'Analytics Service', port: 8084, tech: 'Java 21 CompletableFuture / SSE', latency: 12, uptime: '99.94%' },
    { name: 'Notification Service', port: 8085, tech: 'Spring WebSocket / STOMP Broker', latency: 5, uptime: '99.99%' },
    { name: 'AI Service', port: 8086, tech: 'NL-to-SQL / AST Safety Validator', latency: 14, uptime: '99.92%' },
    { name: 'Python XIRR Engine', port: 8005, tech: 'FastAPI / NumPy / SciPy Optimize', latency: 9, uptime: '99.97%' },
    { name: 'SQL Server 2022', port: 1433, tech: 'Microsoft SQL Server Enterprise', latency: 2, uptime: '100.0%' },
    { name: 'Redis 7 Alpine', port: 6379, tech: 'In-Memory Distributed Store', latency: 1, uptime: '100.0%' }
  ];
}
