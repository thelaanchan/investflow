import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-investments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Trade Stocks & ETFs</h1>
          <p class="page-subtitle">Execute transactional BUY and SELL orders with automatic cost-basis adjustment</p>
        </div>
        <button class="btn btn-primary" (click)="showNewModal = true">+ New Asset Investment</button>
      </div>

      <div class="cards-grid">
        <div class="asset-card glass-panel" *ngFor="let inv of investments">
          <div class="asset-header">
            <div>
              <span class="ticker-badge">{{ inv.symbol }}</span>
              <h3 class="asset-title">{{ inv.name }}</h3>
            </div>
            <span class="status-pill">{{ inv.status }}</span>
          </div>

          <div class="asset-body">
            <div class="metric-row">
              <span class="metric-lbl">Total Units</span>
              <strong class="mono">{{ inv.units | number:'1.2-4' }}</strong>
            </div>
            <div class="metric-row">
              <span class="metric-lbl">Current Market Price</span>
              <strong class="mono">&#36;{{ inv.currentNavOrPrice | number:'1.2-2' }}</strong>
            </div>
            <div class="metric-row">
              <span class="metric-lbl">Invested Capital</span>
              <span class="mono">&#36;{{ inv.investedAmount | number:'1.2-2' }}</span>
            </div>
            <div class="metric-row">
              <span class="metric-lbl">Current Valuation</span>
              <strong class="mono positive">&#36;{{ inv.currentValue | number:'1.2-2' }}</strong>
            </div>
          </div>

          <div class="asset-footer">
            <button class="btn btn-success flex-1" (click)="openTrade(inv, 'BUY')">⚡ BUY</button>
            <button class="btn btn-danger flex-1" (click)="openTrade(inv, 'SELL')">SELL</button>
          </div>
        </div>
      </div>

      <!-- Trade Modal -->
      <div class="modal-backdrop" *ngIf="showTradeModal">
        <div class="modal-card glass-panel">
          <div class="modal-header">
            <h3>Execute Order: <span class="text-gradient">{{ selectedInv?.symbol }}</span></h3>
            <button class="close-btn" (click)="showTradeModal = false">✕</button>
          </div>
          <form (ngSubmit)="submitTrade()">
            <div class="trade-toggle">
              <button type="button" class="btn-toggle" [class.buy-on]="tradeType === 'BUY'" (click)="tradeType = 'BUY'">BUY</button>
              <button type="button" class="btn-toggle" [class.sell-on]="tradeType === 'SELL'" (click)="tradeType = 'SELL'">SELL</button>
            </div>
            <div class="form-group">
              <label class="form-label">Units</label>
              <input type="number" step="0.1" min="0.1" class="form-control" [(ngModel)]="units" name="units" required>
            </div>
            <div class="form-group">
              <label class="form-label">Price per Unit ($)</label>
              <input type="number" step="0.01" min="0.01" class="form-control" [(ngModel)]="price" name="price" required>
            </div>
            <button type="submit" class="btn w-full" [ngClass]="tradeType === 'BUY' ? 'btn-success' : 'btn-danger'">
              Execute {{ tradeType }} Order →
            </button>
          </form>
        </div>
      </div>

      <!-- New Investment Modal -->
      <div class="modal-backdrop" *ngIf="showNewModal">
        <div class="modal-card glass-panel">
          <div class="modal-header">
            <h3>Add New Investment Position</h3>
            <button class="close-btn" (click)="showNewModal = false">✕</button>
          </div>
          <form (ngSubmit)="submitNewInvestment()">
            <div class="form-group">
              <label class="form-label">Symbol / Ticker</label>
              <input type="text" class="form-control" [(ngModel)]="newSymbol" name="sym" required placeholder="e.g. GOOGL">
            </div>
            <div class="form-group">
              <label class="form-label">Company / Asset Name</label>
              <input type="text" class="form-control" [(ngModel)]="newName" name="name" required placeholder="e.g. Alphabet Inc.">
            </div>
            <div class="form-group">
              <label class="form-label">Asset Class</label>
              <select class="form-control" [(ngModel)]="newType" name="type">
                <option value="EQUITY">Stock (Equity)</option>
                <option value="MUTUAL_FUND">Mutual Fund / ETF</option>
                <option value="BOND">Fixed Income Bond</option>
                <option value="CRYPTO">Digital Asset / Crypto</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Initial Units</label>
              <input type="number" step="0.1" class="form-control" [(ngModel)]="newUnits" name="units" required>
            </div>
            <div class="form-group">
              <label class="form-label">Price per Unit ($)</label>
              <input type="number" step="0.01" class="form-control" [(ngModel)]="newPrice" name="price" required>
            </div>
            <button type="submit" class="btn btn-primary w-full">Open Position →</button>
          </form>
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
    .cards-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 20px;
    }
    .asset-card {
      padding: 22px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .asset-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
    }
    .ticker-badge {
      font-size: 0.8rem;
      font-weight: 800;
      color: #38bdf8;
      background: rgba(56, 189, 248, 0.15);
      padding: 2px 8px;
      border-radius: 4px;
    }
    .asset-title {
      font-size: 1.15rem;
      font-weight: 700;
      margin-top: 6px;
    }
    .status-pill {
      font-size: 0.7rem;
      font-weight: 700;
      color: #10b981;
      background: rgba(16, 185, 129, 0.12);
      padding: 2px 8px;
      border-radius: 9999px;
    }
    .asset-body {
      display: flex;
      flex-direction: column;
      gap: 10px;
      background: rgba(15, 23, 42, 0.5);
      padding: 14px;
      border-radius: 10px;
    }
    .metric-row {
      display: flex;
      justify-content: space-between;
      font-size: 0.85rem;
    }
    .metric-lbl {
      color: #94a3b8;
    }
    .asset-footer {
      display: flex;
      gap: 10px;
    }
    .flex-1 {
      flex: 1;
    }
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
    .trade-toggle {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 8px;
      margin-bottom: 14px;
    }
    .btn-toggle {
      padding: 8px;
      border-radius: 8px;
      font-weight: 700;
      background: rgba(255,255,255,0.05);
      border: 1px solid var(--border-color);
      color: #94a3b8;
      cursor: pointer;
    }
    .buy-on { background: #059669 !important; color: #fff !important; }
    .sell-on { background: #e11d48 !important; color: #fff !important; }
    .w-full {
      width: 100%;
      margin-top: 10px;
    }
  `]
})
export class InvestmentsComponent implements OnInit {
  investments: any[] = [];
  showTradeModal = false;
  showNewModal = false;
  selectedInv: any = null;
  tradeType: 'BUY' | 'SELL' = 'BUY';
  units = 5;
  price = 150;

  newSymbol = '';
  newName = '';
  newType = 'EQUITY';
  newUnits = 10;
  newPrice = 100;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadInvestments();
  }

  loadInvestments(): void {
    this.api.getInvestments().subscribe({
      next: res => {
        if (res.data) {
          this.investments = res.data;
        }
      },
      error: () => {
        this.investments = [
          { id: 1, symbol: 'AAPL', name: 'Apple Inc.', assetType: 'EQUITY', units: 25, currentNavOrPrice: 228.40, investedAmount: 4387.50, currentValue: 5710.00, status: 'ACTIVE' },
          { id: 2, symbol: 'MSFT', name: 'Microsoft Corporation', assetType: 'EQUITY', units: 15, currentNavOrPrice: 445.20, investedAmount: 5700.00, currentValue: 6678.00, status: 'ACTIVE' },
          { id: 3, symbol: 'VOO', name: 'Vanguard S&P 500 ETF', assetType: 'MUTUAL_FUND', units: 40, currentNavOrPrice: 512.80, investedAmount: 16400.00, currentValue: 20512.00, status: 'ACTIVE' },
          { id: 4, symbol: 'NVDA', name: 'NVIDIA Corporation', assetType: 'EQUITY', units: 30, currentNavOrPrice: 132.50, investedAmount: 2850.00, currentValue: 3975.00, status: 'ACTIVE' }
        ];
      }
    });
  }

  openTrade(inv: any, type: 'BUY' | 'SELL'): void {
    this.selectedInv = inv;
    this.tradeType = type;
    this.price = inv.currentNavOrPrice || 100;
    this.units = 5;
    this.showTradeModal = true;
  }

  submitTrade(): void {
    if (!this.selectedInv) return;
    const action = this.tradeType === 'BUY'
      ? this.api.buyStock(this.selectedInv.id, { units: this.units, price: this.price })
      : this.api.sellStock(this.selectedInv.id, { units: this.units, price: this.price });

    action.subscribe({
      next: () => {
        this.showTradeModal = false;
        this.loadInvestments();
      }
    });
  }

  submitNewInvestment(): void {
    this.api.createInvestment({
      portfolioId: 1,
      symbol: this.newSymbol,
      name: this.newName,
      assetType: this.newType,
      units: this.newUnits,
      pricePerUnit: this.newPrice
    }).subscribe({
      next: () => {
        this.showNewModal = false;
        this.loadInvestments();
      }
    });
  }
}
