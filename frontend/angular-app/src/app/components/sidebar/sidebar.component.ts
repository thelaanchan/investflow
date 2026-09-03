import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside class="sidebar glass-panel">
      <div class="nav-section">
        <span class="section-label">PORTFOLIO HUB</span>
        <nav class="nav-links">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
            <span class="icon">📊</span>
            <span class="label">Executive Dashboard</span>
          </a>
          <a routerLink="/portfolios" routerLinkActive="active" class="nav-item">
            <span class="icon">💼</span>
            <span class="label">Portfolios</span>
          </a>
          <a routerLink="/holdings" routerLinkActive="active" class="nav-item">
            <span class="icon">📈</span>
            <span class="label">Holdings & Positions</span>
          </a>
        </nav>
      </div>

      <div class="nav-section">
        <span class="section-label">TRADE & AUTOMATION</span>
        <nav class="nav-links">
          <a routerLink="/investments" routerLinkActive="active" class="nav-item">
            <span class="icon">⚡</span>
            <span class="label">Trade Stocks & ETFs</span>
          </a>
          <a routerLink="/sips" routerLinkActive="active" class="nav-item">
            <span class="icon">🔄</span>
            <span class="label">Recurring SIP Plans</span>
          </a>
          <a routerLink="/transactions" routerLinkActive="active" class="nav-item">
            <span class="icon">📜</span>
            <span class="label">Trade Audit Trail</span>
          </a>
        </nav>
      </div>

      <div class="nav-section">
        <span class="section-label">QUANT & INTELLIGENCE</span>
        <nav class="nav-links">
          <a routerLink="/analytics" routerLinkActive="active" class="nav-item">
            <span class="icon">🎯</span>
            <span class="label">Performance & XIRR</span>
          </a>
          <a routerLink="/xray" routerLinkActive="active" class="nav-item">
            <span class="icon">🔬</span>
            <span class="label">Portfolio X-Ray</span>
          </a>
          <a routerLink="/ai-assistant" routerLinkActive="active" class="nav-item ai-link">
            <span class="icon">✨</span>
            <span class="label">AI Financial Copilot</span>
            <span class="mini-tag">NLP-SQL</span>
          </a>
        </nav>
      </div>

      <div class="nav-section admin-section">
        <span class="section-label">INFRASTRUCTURE</span>
        <nav class="nav-links">
          <a routerLink="/admin" routerLinkActive="active" class="nav-item">
            <span class="icon">🛡️</span>
            <span class="label">Platform Health</span>
          </a>
        </nav>
      </div>

      <div class="sidebar-footer">
        <div class="sys-badge">
          <span class="pulse-indicator"></span>
          <span>Gateway Active (8080)</span>
        </div>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 250px;
      margin: 16px 0 16px 20px;
      padding: 20px 14px;
      display: flex;
      flex-direction: column;
      gap: 22px;
      min-height: calc(100vh - 100px);
    }
    .nav-section {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .section-label {
      font-size: 0.68rem;
      font-weight: 700;
      color: #64748b;
      letter-spacing: 0.08em;
      padding: 0 12px;
    }
    .nav-links {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 9px 12px;
      border-radius: 10px;
      color: #94a3b8;
      text-decoration: none;
      font-size: 0.88rem;
      font-weight: 500;
      transition: all 0.2s;
    }
    .nav-item:hover {
      background: rgba(255, 255, 255, 0.06);
      color: #f8fafc;
      transform: translateX(2px);
    }
    .nav-item.active {
      background: linear-gradient(135deg, rgba(37, 99, 235, 0.2) 0%, rgba(6, 182, 212, 0.15) 100%);
      color: #38bdf8;
      font-weight: 600;
      border: 1px solid rgba(56, 189, 248, 0.3);
      box-shadow: 0 0 15px rgba(56, 189, 248, 0.15);
    }
    .icon {
      font-size: 1.05rem;
    }
    .ai-link {
      background: rgba(139, 92, 246, 0.08);
      border: 1px dashed rgba(139, 92, 246, 0.25);
    }
    .ai-link:hover {
      border-color: #a855f7;
    }
    .mini-tag {
      font-size: 0.62rem;
      font-weight: 700;
      background: linear-gradient(135deg, #8b5cf6 0%, #ec4899 100%);
      color: #fff;
      padding: 1px 5px;
      border-radius: 4px;
      margin-left: auto;
    }
    .admin-section {
      margin-top: auto;
    }
    .sidebar-footer {
      padding-top: 14px;
      border-top: 1px solid rgba(255, 255, 255, 0.06);
    }
    .sys-badge {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 0.72rem;
      color: #64748b;
      padding: 4px 8px;
    }
  `]
})
export class SidebarComponent {}
