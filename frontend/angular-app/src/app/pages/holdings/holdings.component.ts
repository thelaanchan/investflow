import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-holdings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Holdings & Asset Positions</h1>
          <p class="page-subtitle">Real-time valuation, cost-basis breakdown, and unrealized profit metrics</p>
        </div>
      </div>

      <div class="table-card glass-panel">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>TICKER</th>
                <th>NAME</th>
                <th>ASSET TYPE</th>
                <th>QUANTITY</th>
                <th>BUY PRICE</th>
                <th>MARKET PRICE</th>
                <th>INVESTED</th>
                <th>VALUATION</th>
                <th>TOTAL GAIN</th>
                <th>RETURN %</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let h of holdings">
                <td><strong class="symbol-badge">{{ h.assetSymbol }}</strong></td>
                <td>{{ h.assetName }}</td>
                <td><span class="badge-neutral">{{ h.assetType }}</span></td>
                <td class="mono">{{ h.quantity | number:'1.2-4' }}</td>
                <td class="mono">&#36;{{ h.averageBuyPrice | number:'1.2-2' }}</td>
                <td class="mono font-semibold">&#36;{{ h.currentPrice | number:'1.2-2' }}</td>
                <td class="mono">&#36;{{ h.totalInvested | number:'1.2-2' }}</td>
                <td class="mono font-semibold">&#36;{{ h.currentValue | number:'1.2-2' }}</td>
                <td class="mono" [ngClass]="h.profitOrLoss >= 0 ? 'positive' : 'negative'">
                  {{ h.profitOrLoss >= 0 ? '+' : '' }}&#36;{{ h.profitOrLoss | number:'1.2-2' }}
                </td>
                <td>
                  <span [ngClass]="h.returnsPercentage >= 0 ? 'badge-positive' : 'badge-negative'">
                    {{ h.returnsPercentage >= 0 ? '+' : '' }}{{ h.returnsPercentage }}%
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
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
    .table-card {
      padding: 16px;
    }
    .symbol-badge {
      color: #38bdf8;
      font-weight: 700;
    }
    .font-semibold {
      font-weight: 600;
    }
  `]
})
export class HoldingsComponent implements OnInit {
  holdings: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getHoldings(1).subscribe({
      next: res => {
        if (res.data) {
          this.holdings = res.data;
        }
      },
      error: () => {
        this.holdings = [
          { assetSymbol: 'AAPL', assetName: 'Apple Inc.', assetType: 'EQUITY', quantity: 25, averageBuyPrice: 175.50, currentPrice: 228.40, totalInvested: 4387.50, currentValue: 5710.00, profitOrLoss: 1322.50, returnsPercentage: 30.14 },
          { assetSymbol: 'MSFT', assetName: 'Microsoft Corporation', assetType: 'EQUITY', quantity: 15, averageBuyPrice: 380.00, currentPrice: 445.20, totalInvested: 5700.00, currentValue: 6678.00, profitOrLoss: 978.00, returnsPercentage: 17.16 },
          { assetSymbol: 'VOO', assetName: 'Vanguard S&P 500 ETF', assetType: 'MUTUAL_FUND', quantity: 40, averageBuyPrice: 410.00, currentPrice: 512.80, totalInvested: 16400.00, currentValue: 20512.00, profitOrLoss: 4112.00, returnsPercentage: 25.07 },
          { assetSymbol: 'NVDA', assetName: 'NVIDIA Corporation', assetType: 'EQUITY', quantity: 30, averageBuyPrice: 95.00, currentPrice: 132.50, totalInvested: 2850.00, currentValue: 3975.00, profitOrLoss: 1125.00, returnsPercentage: 39.47 }
        ];
      }
    });
  }
}
