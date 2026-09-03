import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-xray',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Portfolio X-Ray & Deep Diversification</h1>
          <p class="page-subtitle">Multidimensional risk analysis, sector exposure, and market cap concentration</p>
        </div>
        <div class="score-pill">
          <span class="score-label">Diversification Score</span>
          <strong class="score-num text-gradient">84.5 / 100</strong>
        </div>
      </div>

      <div class="xray-grid">
        <!-- Sector Exposure -->
        <div class="xray-card glass-panel">
          <div class="card-header">
            <h3>Sector Weighting & Concentration</h3>
            <span class="badge-neutral">5 Sectors</span>
          </div>

          <div class="breakdown-list">
            <div class="breakdown-item" *ngFor="let s of sectors">
              <div class="item-head">
                <span class="item-name">{{ s.name }}</span>
                <span class="item-val mono">{{ s.pct }}%</span>
              </div>
              <div class="bar-track">
                <div class="bar-fill" [style.width.%]="s.pct" [style.background]="s.color"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Market Capitalization -->
        <div class="xray-card glass-panel">
          <div class="card-header">
            <h3>Market Capitalization Spread</h3>
            <span class="badge-neutral">Equities</span>
          </div>

          <div class="breakdown-list">
            <div class="breakdown-item" *ngFor="let m of marketCaps">
              <div class="item-head">
                <span class="item-name">{{ m.name }}</span>
                <span class="item-val mono">{{ m.pct }}%</span>
              </div>
              <div class="bar-track">
                <div class="bar-fill" [style.width.%]="m.pct" [style.background]="m.color"></div>
              </div>
            </div>
          </div>

          <div class="xray-insight">
            <div class="insight-icon">💡</div>
            <p class="insight-text">
              Strong large-cap anchor provides balance against volatility while mid-cap tech grants growth upside.
            </p>
          </div>
        </div>
      </div>

      <!-- Regional & Global Exposure -->
      <div class="geo-card glass-panel">
        <div class="card-header">
          <h3>Geographical Allocation</h3>
        </div>
        <div class="geo-grid">
          <div class="geo-item">
            <span class="geo-region">United States</span>
            <span class="geo-pct mono positive">86.0%</span>
            <span class="geo-desc">North American Equities & S&P Index</span>
          </div>
          <div class="geo-item">
            <span class="geo-region">Developed International</span>
            <span class="geo-pct mono">10.5%</span>
            <span class="geo-desc">Europe & Asia-Pacific Blue Chips</span>
          </div>
          <div class="geo-item">
            <span class="geo-region">Emerging Markets</span>
            <span class="geo-pct mono">3.5%</span>
            <span class="geo-desc">High-growth developing economies</span>
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
    .score-pill {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      background: rgba(15, 23, 42, 0.7);
      padding: 8px 16px;
      border-radius: 12px;
      border: 1px solid var(--border-color);
    }
    .score-label {
      font-size: 0.72rem;
      color: #64748b;
    }
    .score-num {
      font-size: 1.35rem;
      font-weight: 800;
    }
    .xray-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
    }
    .xray-card, .geo-card {
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .card-header h3 {
      font-size: 1.15rem;
      font-weight: 700;
    }
    .breakdown-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .item-head {
      display: flex;
      justify-content: space-between;
      font-size: 0.85rem;
      color: #f8fafc;
      margin-bottom: 6px;
    }
    .item-val {
      font-weight: 600;
      color: #38bdf8;
    }
    .bar-track {
      width: 100%;
      height: 8px;
      background: rgba(255, 255, 255, 0.06);
      border-radius: 4px;
      overflow: hidden;
    }
    .bar-fill {
      height: 100%;
      border-radius: 4px;
      transition: width 0.4s ease;
    }
    .xray-insight {
      display: flex;
      gap: 12px;
      background: rgba(56, 189, 248, 0.08);
      border: 1px solid rgba(56, 189, 248, 0.2);
      padding: 12px 14px;
      border-radius: 10px;
      margin-top: 10px;
    }
    .insight-icon {
      font-size: 1.2rem;
    }
    .insight-text {
      font-size: 0.82rem;
      color: #94a3b8;
      line-height: 1.4;
    }
    .geo-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 16px;
    }
    .geo-item {
      background: rgba(15, 23, 42, 0.5);
      padding: 16px;
      border-radius: 12px;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .geo-region {
      font-size: 0.95rem;
      font-weight: 700;
    }
    .geo-pct {
      font-size: 1.6rem;
      font-weight: 800;
    }
    .geo-desc {
      font-size: 0.75rem;
      color: #64748b;
    }
  `]
})
export class XrayComponent implements OnInit {
  sectors = [
    { name: 'Information Technology', pct: 46.5, color: '#38bdf8' },
    { name: 'Consumer Discretionary', pct: 18.2, color: '#818cf8' },
    { name: 'Financials', pct: 14.3, color: '#10b981' },
    { name: 'Communication Services', pct: 11.0, color: '#f59e0b' },
    { name: 'Fixed Income / Bonds', pct: 10.0, color: '#ec4899' }
  ];

  marketCaps = [
    { name: 'Mega & Large Cap (> $10B)', pct: 78.5, color: '#2563eb' },
    { name: 'Mid Cap ($2B - $10B)', pct: 15.1, color: '#06b6d4' },
    { name: 'Small Cap (< $2B)', pct: 6.4, color: '#a855f7' }
  ];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getXRay(1).subscribe({
      next: res => {
        // Can bind dynamic payload if required
      }
    });
  }
}
