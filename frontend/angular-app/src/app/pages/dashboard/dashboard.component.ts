import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="dashboard-page">
      <!-- Top Title & Controls -->
      <div class="page-header">
        <div>
          <h1 class="page-title">Executive Investment Dashboard</h1>
          <p class="page-subtitle">Real-time performance metrics, multithreaded portfolio analytics & live market feed</p>
        </div>
        <div class="header-actions">
          <div class="live-stream-badge">
            <span class="pulse-indicator"></span>
            <span>SSE Stream Active</span>
          </div>
          <button class="btn btn-primary" (click)="openQuickBuy()">
            <span>⚡ Quick Trade</span>
          </button>
        </div>
      </div>

      <!-- KPI Summary Cards -->
      <div class="kpi-grid">
        <div class="kpi-card glass-panel">
          <div class="kpi-label">TOTAL PORTFOLIO VALUE</div>
          <div class="kpi-value mono">&#36;{{ currentValue | number:'1.2-2' }}</div>
          <div class="kpi-meta positive">
            <span class="badge-positive">▲ +{{ dayChangePercentage }}% today</span>
            <span class="meta-note">Live SSE Tick</span>
          </div>
        </div>

        <div class="kpi-card glass-panel">
          <div class="kpi-label">TOTAL INVESTED CAPITAL</div>
          <div class="kpi-value mono">&#36;{{ totalInvested | number:'1.2-2' }}</div>
          <div class="kpi-meta">
            <span class="badge-neutral">4 Active Positions</span>
          </div>
        </div>

        <div class="kpi-card glass-panel">
          <div class="kpi-label">UNREALIZED PROFIT / GAIN</div>
          <div class="kpi-value mono positive">+&#36;{{ totalProfitLoss | number:'1.2-2' }}</div>
          <div class="kpi-meta">
            <span class="badge-positive">+{{ returnsPercentage }}% Total ROI</span>
          </div>
        </div>

        <div class="kpi-card glass-panel highlight-kpi">
          <div class="kpi-label">ANNUALIZED XIRR</div>
          <div class="kpi-value mono text-gradient">{{ xirrPercentage }}%</div>
          <div class="kpi-meta">
            <span class="badge-positive">Python SciPy Engine</span>
          </div>
        </div>
      </div>

      <!-- Main Visual Section: Interactive Chart & Allocation -->
      <div class="charts-grid">
        <!-- SVG Performance Chart -->
        <div class="chart-card glass-panel">
          <div class="card-header">
            <div>
              <h3>Portfolio Value vs Benchmark</h3>
              <p class="card-subtitle">6-Month growth trajectory compared to S&P 500 Index</p>
            </div>
            <div class="chart-legend">
              <span class="legend-item"><span class="dot dot-portfolio"></span> InvestFlow (+22.2%)</span>
              <span class="legend-item"><span class="dot dot-benchmark"></span> S&P 500 (+14.2%)</span>
            </div>
          </div>

          <div class="svg-chart-container">
            <svg viewBox="0 0 700 220" class="performance-chart">
              <defs>
                <linearGradient id="chartGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#38bdf8" stop-opacity="0.35"/>
                  <stop offset="100%" stop-color="#38bdf8" stop-opacity="0.0"/>
                </linearGradient>
              </defs>
              <!-- Grid Lines -->
              <line x1="40" y1="30" x2="680" y2="30" stroke="rgba(255,255,255,0.06)" />
              <line x1="40" y1="90" x2="680" y2="90" stroke="rgba(255,255,255,0.06)" />
              <line x1="40" y1="150" x2="680" y2="150" stroke="rgba(255,255,255,0.06)" />
              <line x1="40" y1="190" x2="680" y2="190" stroke="rgba(255,255,255,0.1)" />

              <!-- Benchmark Line (Dotted Green/Gray) -->
              <path d="M 60 180 L 160 165 L 260 155 L 360 135 L 460 120 L 560 105 L 660 85"
                    fill="none" stroke="#64748b" stroke-width="2" stroke-dasharray="4 4" />

              <!-- Portfolio Gradient Fill -->
              <path d="M 60 180 L 160 150 L 260 130 L 360 100 L 460 70 L 560 55 L 660 40 L 660 190 L 60 190 Z"
                    fill="url(#chartGrad)" />

              <!-- Portfolio Line -->
              <path d="M 60 180 L 160 150 L 260 130 L 360 100 L 460 70 L 560 55 L 660 40"
                    fill="none" stroke="#38bdf8" stroke-width="3.5" stroke-linecap="round" />

              <!-- Data Point Circles -->
              <circle cx="60" cy="180" r="4" fill="#38bdf8" />
              <circle cx="160" cy="150" r="4" fill="#38bdf8" />
              <circle cx="260" cy="130" r="4" fill="#38bdf8" />
              <circle cx="360" cy="100" r="4" fill="#38bdf8" />
              <circle cx="460" cy="70" r="4" fill="#38bdf8" />
              <circle cx="560" cy="55" r="4" fill="#38bdf8" />
              <circle cx="660" cy="40" r="6" fill="#10b981" stroke="#fff" stroke-width="2" />
            </svg>
            <div class="chart-x-axis">
              <span>Oct</span>
              <span>Nov</span>
              <span>Dec</span>
              <span>Jan</span>
              <span>Feb</span>
              <span>Mar</span>
              <span>Current</span>
            </div>
          </div>
        </div>

        <!-- Asset Allocation Card -->
        <div class="allocation-card glass-panel">
          <div class="card-header">
            <h3>Asset Allocation</h3>
          </div>
          <div class="allocation-bars">
            <div class="alloc-item">
              <div class="alloc-info">
                <span>US Equities (AAPL, MSFT, NVDA)</span>
                <strong class="mono">65.2%</strong>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" style="width: 65.2%; background: #3b82f6;"></div>
              </div>
            </div>

            <div class="alloc-item">
              <div class="alloc-info">
                <span>Mutual Funds / ETFs (VOO, VTI)</span>
                <strong class="mono">28.4%</strong>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" style="width: 28.4%; background: #10b981;"></div>
              </div>
            </div>

            <div class="alloc-item">
              <div class="alloc-info">
                <span>Fixed Income / Bonds (BND)</span>
                <strong class="mono">6.4%</strong>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" style="width: 6.4%; background: #f59e0b;"></div>
              </div>
            </div>
          </div>

          <div class="allocation-summary">
            <div class="summary-box">
              <span class="sub-label">Alpha</span>
              <span class="sub-val positive mono">+8.03%</span>
            </div>
            <div class="summary-box">
              <span class="sub-label">Beta</span>
              <span class="sub-val mono">1.12</span>
            </div>
            <div class="summary-box">
              <span class="sub-label">Sharpe</span>
              <span class="sub-val mono">1.85</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Top Holdings Table -->
      <div class="holdings-section glass-panel">
        <div class="card-header">
          <div>
            <h3>Active Holdings & Real-Time Valuations</h3>
            <p class="card-subtitle">Live mark-to-market prices and positions</p>
          </div>
          <a routerLink="/holdings" class="btn btn-secondary btn-sm">View All Holdings →</a>
        </div>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>ASSET / TICKER</th>
                <th>TYPE</th>
                <th>UNITS</th>
                <th>AVG PRICE</th>
                <th>CURRENT PRICE</th>
                <th>TOTAL INVESTED</th>
                <th>CURRENT VALUE</th>
                <th>UNREALIZED P&L</th>
                <th>ACTIONS</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let h of holdings">
                <td>
                  <div class="asset-cell">
                    <span class="symbol-badge">{{ h.assetSymbol }}</span>
                    <span class="asset-name">{{ h.assetName }}</span>
                  </div>
                </td>
                <td><span class="badge-neutral">{{ h.assetType }}</span></td>
                <td class="mono">{{ h.quantity | number:'1.2-4' }}</td>
                <td class="mono">&#36;{{ h.averageBuyPrice | number:'1.2-2' }}</td>
                <td class="mono font-semibold">&#36;{{ h.currentPrice | number:'1.2-2' }}</td>
                <td class="mono">&#36;{{ h.totalInvested | number:'1.2-2' }}</td>
                <td class="mono font-semibold">&#36;{{ h.currentValue | number:'1.2-2' }}</td>
                <td>
                  <span [ngClass]="h.profitOrLoss >= 0 ? 'badge-positive' : 'badge-negative'">
                    {{ h.profitOrLoss >= 0 ? '+' : '' }}&#36;{{ h.profitOrLoss | number:'1.2-2' }} ({{ h.returnsPercentage }}%)
                  </span>
                </td>
                <td>
                  <div class="action-btns">
                    <button class="btn-action buy" (click)="openTrade(h, 'BUY')">Buy</button>
                    <button class="btn-action sell" (click)="openTrade(h, 'SELL')">Sell</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Trade Modal -->
      <div class="modal-backdrop" *ngIf="showTradeModal">
        <div class="modal-card glass-panel">
          <div class="modal-header">
            <h3>Execute Order: <span class="text-gradient">{{ selectedHolding?.assetSymbol }}</span></h3>
            <button class="close-btn" (click)="showTradeModal = false">✕</button>
          </div>

          <form (ngSubmit)="executeTrade()">
            <div class="trade-type-toggle">
              <button type="button" class="toggle-btn" [class.active-buy]="tradeType === 'BUY'" (click)="tradeType = 'BUY'">BUY ORDER</button>
              <button type="button" class="toggle-btn" [class.active-sell]="tradeType === 'SELL'" (click)="tradeType = 'SELL'">SELL ORDER</button>
            </div>

            <div class="form-group">
              <label class="form-label">Units / Quantity</label>
              <input type="number" step="0.1" min="0.1" class="form-control" [(ngModel)]="tradeUnits" name="units" (input)="calcOrderTotal()" required>
            </div>

            <div class="form-group">
              <label class="form-label">Execution Price ($)</label>
              <input type="number" step="0.01" min="0.01" class="form-control" [(ngModel)]="tradePrice" name="price" (input)="calcOrderTotal()" required>
            </div>

            <div class="order-summary">
              <span>Estimated Order Value:</span>
              <strong class="mono">&#36;{{ orderTotal | number:'1.2-2' }}</strong>
            </div>

            <button type="submit" class="btn w-full" [ngClass]="tradeType === 'BUY' ? 'btn-success' : 'btn-danger'">
              Submit {{ tradeType }} Order →
            </button>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-page {
      display: flex;
      flex-direction: column;
      gap: 24px;
      padding: 20px 24px 40px;
    }
    .page-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .page-title {
      font-size: 1.65rem;
      font-weight: 800;
      letter-spacing: -0.5px;
    }
    .page-subtitle {
      font-size: 0.88rem;
      color: #94a3b8;
      margin-top: 4px;
    }
    .header-actions {
      display: flex;
      align-items: center;
      gap: 14px;
    }
    .live-stream-badge {
      display: flex;
      align-items: center;
      gap: 8px;
      background: rgba(16, 185, 129, 0.12);
      border: 1px solid rgba(16, 185, 129, 0.3);
      color: #10b981;
      padding: 6px 14px;
      border-radius: 9999px;
      font-size: 0.78rem;
      font-weight: 600;
    }
    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 18px;
    }
    .kpi-card {
      padding: 20px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .highlight-kpi {
      border-color: rgba(56, 189, 248, 0.3);
      background: linear-gradient(135deg, rgba(17, 24, 39, 0.85) 0%, rgba(30, 41, 59, 0.9) 100%);
    }
    .kpi-label {
      font-size: 0.72rem;
      font-weight: 700;
      color: #64748b;
      letter-spacing: 0.06em;
    }
    .kpi-value {
      font-size: 1.7rem;
      font-weight: 800;
      letter-spacing: -0.5px;
    }
    .kpi-meta {
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .meta-note {
      font-size: 0.72rem;
      color: #64748b;
    }
    .charts-grid {
      display: grid;
      grid-template-columns: 2fr 1fr;
      gap: 18px;
    }
    .chart-card, .allocation-card {
      padding: 22px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .card-header h3 {
      font-size: 1.1rem;
      font-weight: 700;
    }
    .card-subtitle {
      font-size: 0.8rem;
      color: #94a3b8;
    }
    .chart-legend {
      display: flex;
      gap: 14px;
      font-size: 0.8rem;
    }
    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #94a3b8;
    }
    .dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
    }
    .dot-portfolio { background: #38bdf8; }
    .dot-benchmark { background: #64748b; }
    .svg-chart-container {
      width: 100%;
      height: 220px;
    }
    .performance-chart {
      width: 100%;
      height: 180px;
    }
    .chart-x-axis {
      display: flex;
      justify-content: space-between;
      padding: 0 40px;
      font-size: 0.72rem;
      color: #64748b;
    }
    .allocation-bars {
      display: flex;
      flex-direction: column;
      gap: 14px;
    }
    .alloc-info {
      display: flex;
      justify-content: space-between;
      font-size: 0.82rem;
      color: #94a3b8;
      margin-bottom: 6px;
    }
    .progress-bar {
      width: 100%;
      height: 8px;
      background: rgba(255, 255, 255, 0.06);
      border-radius: 4px;
      overflow: hidden;
    }
    .progress-fill {
      height: 100%;
      border-radius: 4px;
      transition: width 0.4s ease;
    }
    .allocation-summary {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 10px;
      margin-top: auto;
      padding-top: 14px;
      border-top: 1px solid rgba(255, 255, 255, 0.06);
    }
    .summary-box {
      background: rgba(15, 23, 42, 0.6);
      padding: 8px 10px;
      border-radius: 8px;
      text-align: center;
      display: flex;
      flex-direction: column;
    }
    .sub-label {
      font-size: 0.68rem;
      color: #64748b;
    }
    .sub-val {
      font-size: 0.95rem;
      font-weight: 700;
    }
    .holdings-section {
      padding: 22px;
    }
    .btn-sm {
      padding: 6px 12px;
      font-size: 0.8rem;
    }
    .asset-cell {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .symbol-badge {
      font-weight: 700;
      color: #38bdf8;
      background: rgba(56, 189, 248, 0.12);
      padding: 3px 8px;
      border-radius: 6px;
      font-size: 0.82rem;
    }
    .asset-name {
      font-size: 0.85rem;
      color: #94a3b8;
    }
    .font-semibold {
      font-weight: 600;
    }
    .action-btns {
      display: flex;
      gap: 6px;
    }
    .btn-action {
      padding: 4px 10px;
      border-radius: 6px;
      font-size: 0.75rem;
      font-weight: 600;
      cursor: pointer;
      border: none;
    }
    .btn-action.buy {
      background: rgba(16, 185, 129, 0.15);
      color: #10b981;
      border: 1px solid rgba(16, 185, 129, 0.3);
    }
    .btn-action.buy:hover {
      background: #10b981;
      color: #fff;
    }
    .btn-action.sell {
      background: rgba(244, 63, 94, 0.15);
      color: #f43f5e;
      border: 1px solid rgba(244, 63, 94, 0.3);
    }
    .btn-action.sell:hover {
      background: #f43f5e;
      color: #fff;
    }
    /* Modal */
    .modal-backdrop {
      position: fixed;
      inset: 0;
      background: rgba(0,0,0,0.7);
      backdrop-filter: blur(8px);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }
    .modal-card {
      width: 100%;
      max-width: 440px;
      padding: 28px;
    }
    .modal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 18px;
    }
    .close-btn {
      background: none;
      border: none;
      color: #94a3b8;
      font-size: 1.2rem;
      cursor: pointer;
    }
    .trade-type-toggle {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 8px;
      margin-bottom: 16px;
    }
    .toggle-btn {
      padding: 9px;
      border-radius: 8px;
      font-weight: 700;
      font-size: 0.82rem;
      border: 1px solid var(--border-color);
      background: rgba(255,255,255,0.04);
      color: #94a3b8;
      cursor: pointer;
    }
    .active-buy {
      background: #059669 !important;
      color: #fff !important;
      border-color: #10b981 !important;
    }
    .active-sell {
      background: #e11d48 !important;
      color: #fff !important;
      border-color: #f43f5e !important;
    }
    .order-summary {
      display: flex;
      justify-content: space-between;
      background: rgba(15, 23, 42, 0.6);
      padding: 12px;
      border-radius: 8px;
      margin-bottom: 18px;
      font-size: 0.88rem;
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  currentValue = 35859.00;
  totalInvested = 29337.50;
  totalProfitLoss = 6521.50;
  returnsPercentage = 22.23;
  xirrPercentage = 22.85;
  dayChangePercentage = '+0.96';

  holdings: any[] = [];
  sseSubscription?: Subscription;

  // Trade Modal
  showTradeModal = false;
  selectedHolding: any = null;
  tradeType: 'BUY' | 'SELL' = 'BUY';
  tradeUnits = 5;
  tradePrice = 150;
  orderTotal = 750;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadData();
    this.initSseStream();
  }

  ngOnDestroy(): void {
    this.sseSubscription?.unsubscribe();
  }

  loadData(): void {
    this.api.getPortfolios().subscribe({
      next: res => {
        if (res.data && res.data.length > 0) {
          const portfolio = res.data[0];
          this.totalInvested = portfolio.totalInvested || 29337.50;
          this.currentValue = portfolio.currentValue || 35859.00;
          this.totalProfitLoss = portfolio.totalProfitLoss || 6521.50;
          this.returnsPercentage = portfolio.returnsPercentage || 22.23;

          this.api.getHoldings(portfolio.id).subscribe({
            next: hRes => {
              if (hRes.data) {
                this.holdings = hRes.data;
              }
            }
          });
        }
      },
      error: () => {
        // Mock fallback if offline
        this.holdings = [
          { assetSymbol: 'AAPL', assetName: 'Apple Inc.', assetType: 'EQUITY', quantity: 25, averageBuyPrice: 175.50, currentPrice: 228.40, totalInvested: 4387.50, currentValue: 5710.00, profitOrLoss: 1322.50, returnsPercentage: 30.14 },
          { assetSymbol: 'MSFT', assetName: 'Microsoft Corporation', assetType: 'EQUITY', quantity: 15, averageBuyPrice: 380.00, currentPrice: 445.20, totalInvested: 5700.00, currentValue: 6678.00, profitOrLoss: 978.00, returnsPercentage: 17.16 },
          { assetSymbol: 'VOO', assetName: 'Vanguard S&P 500 ETF', assetType: 'MUTUAL_FUND', quantity: 40, averageBuyPrice: 410.00, currentPrice: 512.80, totalInvested: 16400.00, currentValue: 20512.00, profitOrLoss: 4112.00, returnsPercentage: 25.07 },
          { assetSymbol: 'NVDA', assetName: 'NVIDIA Corporation', assetType: 'EQUITY', quantity: 30, averageBuyPrice: 95.00, currentPrice: 132.50, totalInvested: 2850.00, currentValue: 3975.00, profitOrLoss: 1125.00, returnsPercentage: 39.47 }
        ];
      }
    });
  }

  initSseStream(): void {
    this.sseSubscription = this.api.subscribeSseTicks(1).subscribe({
      next: (tick: any) => {
        if (tick && tick.currentValue) {
          this.currentValue = Number(tick.currentValue);
          this.totalProfitLoss = this.currentValue - this.totalInvested;
        }
      },
      error: () => {}
    });
  }

  openQuickBuy(): void {
    if (this.holdings.length > 0) {
      this.openTrade(this.holdings[0], 'BUY');
    }
  }

  openTrade(holding: any, type: 'BUY' | 'SELL'): void {
    this.selectedHolding = holding;
    this.tradeType = type;
    this.tradePrice = holding.currentPrice || 100;
    this.tradeUnits = 5;
    this.calcOrderTotal();
    this.showTradeModal = true;
  }

  calcOrderTotal(): void {
    this.orderTotal = (this.tradeUnits || 0) * (this.tradePrice || 0);
  }

  executeTrade(): void {
    if (!this.selectedHolding) return;

    if (this.tradeType === 'BUY') {
      this.api.buyStock(this.selectedHolding.id, { units: this.tradeUnits, price: this.tradePrice }).subscribe({
        next: () => {
          this.showTradeModal = false;
          this.loadData();
        },
        error: () => {
          this.showTradeModal = false;
        }
      });
    } else {
      this.api.sellStock(this.selectedHolding.id, { units: this.tradeUnits, price: this.tradePrice }).subscribe({
        next: () => {
          this.showTradeModal = false;
          this.loadData();
        },
        error: () => {
          this.showTradeModal = false;
        }
      });
    }
  }
}
