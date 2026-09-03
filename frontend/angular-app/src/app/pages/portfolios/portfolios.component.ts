import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-portfolios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Portfolios Management</h1>
          <p class="page-subtitle">Track, segment, and structure your wealth accounts</p>
        </div>
        <button class="btn btn-primary" (click)="showCreateModal = true">+ Create New Portfolio</button>
      </div>

      <div class="portfolios-grid">
        <div class="portfolio-card glass-panel" *ngFor="let p of portfolios">
          <div class="card-top">
            <div class="p-type-badge">{{ p.type }}</div>
            <div class="p-date">{{ p.createdAt | date:'mediumDate' }}</div>
          </div>
          <h3 class="p-name">{{ p.name }}</h3>
          <p class="p-desc">{{ p.description }}</p>

          <div class="p-stats">
            <div class="stat-col">
              <span class="stat-label">Invested</span>
              <span class="stat-val mono">&#36;{{ p.totalInvested | number:'1.2-2' }}</span>
            </div>
            <div class="stat-col">
              <span class="stat-label">Current Value</span>
              <span class="stat-val mono font-semibold">&#36;{{ p.currentValue | number:'1.2-2' }}</span>
            </div>
            <div class="stat-col">
              <span class="stat-label">Returns</span>
              <span class="stat-val positive mono">+{{ p.returnsPercentage }}%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Create Modal -->
      <div class="modal-backdrop" *ngIf="showCreateModal">
        <div class="modal-card glass-panel">
          <div class="modal-header">
            <h3>Create New Portfolio</h3>
            <button class="close-btn" (click)="showCreateModal = false">✕</button>
          </div>
          <form (ngSubmit)="createPortfolio()">
            <div class="form-group">
              <label class="form-label">Portfolio Name</label>
              <input type="text" class="form-control" [(ngModel)]="newName" name="name" required placeholder="e.g. Dividend Yield 2026">
            </div>
            <div class="form-group">
              <label class="form-label">Strategy / Type</label>
              <select class="form-control" [(ngModel)]="newType" name="type">
                <option value="GROWTH">Growth (Capital Appreciation)</option>
                <option value="BALANCED">Balanced (Equity & Fixed Income)</option>
                <option value="AGGRESSIVE">Aggressive (High-beta Tech)</option>
                <option value="INCOME">Income (Dividends & Yield)</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Description</label>
              <textarea class="form-control" rows="3" [(ngModel)]="newDesc" name="desc" placeholder="Investment mandate and risk criteria"></textarea>
            </div>
            <button type="submit" class="btn btn-primary w-full">Initialize Portfolio →</button>
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
    .portfolios-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
      gap: 20px;
    }
    .portfolio-card {
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    .card-top {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .p-type-badge {
      background: rgba(56, 189, 248, 0.15);
      color: #38bdf8;
      border: 1px solid rgba(56, 189, 248, 0.3);
      padding: 3px 8px;
      border-radius: 6px;
      font-size: 0.72rem;
      font-weight: 700;
    }
    .p-date {
      font-size: 0.75rem;
      color: #64748b;
    }
    .p-name {
      font-size: 1.2rem;
      font-weight: 700;
    }
    .p-desc {
      font-size: 0.85rem;
      color: #94a3b8;
      min-height: 40px;
    }
    .p-stats {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 10px;
      margin-top: 10px;
      padding-top: 14px;
      border-top: 1px solid rgba(255, 255, 255, 0.06);
    }
    .stat-col {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .stat-label {
      font-size: 0.7rem;
      color: #64748b;
    }
    .stat-val {
      font-size: 0.95rem;
      font-weight: 600;
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
    .w-full {
      width: 100%;
      margin-top: 10px;
    }
  `]
})
export class PortfoliosComponent implements OnInit {
  portfolios: any[] = [];
  showCreateModal = false;
  newName = '';
  newType = 'GROWTH';
  newDesc = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPortfolios();
  }

  loadPortfolios(): void {
    this.api.getPortfolios().subscribe({
      next: res => {
        if (res.data) {
          this.portfolios = res.data;
        }
      },
      error: () => {
        this.portfolios = [
          { id: 1, name: 'Core Growth Wealth', type: 'GROWTH', description: 'High-conviction large cap tech and diversified index ETF portfolio', totalInvested: 29337.50, currentValue: 35859.00, returnsPercentage: 22.23, createdAt: new Date() },
          { id: 2, name: 'Retirement 2050', type: 'BALANCED', description: 'Balanced retirement fund with index equities and fixed income bonds', totalInvested: 31800.00, currentValue: 37705.00, returnsPercentage: 18.57, createdAt: new Date() }
        ];
      }
    });
  }

  createPortfolio(): void {
    this.api.createPortfolio({ name: this.newName, type: this.newType, description: this.newDesc }).subscribe({
      next: () => {
        this.showCreateModal = false;
        this.loadPortfolios();
        this.newName = '';
        this.newDesc = '';
      }
    });
  }
}
