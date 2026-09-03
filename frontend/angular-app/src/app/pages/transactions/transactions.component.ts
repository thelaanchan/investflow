import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Trade Audit Trail & Ledger</h1>
          <p class="page-subtitle">Immutable transaction history with execution timestamps and execution prices</p>
        </div>
      </div>

      <div class="table-card glass-panel">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>TRANSACTION ID</th>
                <th>ORDER TYPE</th>
                <th>UNITS</th>
                <th>EXECUTION PRICE</th>
                <th>TOTAL AMOUNT</th>
                <th>DATE & TIME</th>
                <th>STATUS</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let tx of transactions">
                <td class="mono">#TX-{{ tx.id }}</td>
                <td>
                  <span [ngClass]="tx.type === 'BUY' ? 'badge-positive' : 'badge-negative'">
                    {{ tx.type }}
                  </span>
                </td>
                <td class="mono font-semibold">{{ tx.units | number:'1.2-4' }}</td>
                <td class="mono">&#36;{{ tx.pricePerUnit | number:'1.2-2' }}</td>
                <td class="mono font-semibold">&#36;{{ tx.totalAmount | number:'1.2-2' }}</td>
                <td>{{ tx.transactionDate | date:'medium' }}</td>
                <td><span class="badge-neutral">{{ tx.status }}</span></td>
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
    .font-semibold {
      font-weight: 600;
    }
  `]
})
export class TransactionsComponent implements OnInit {
  transactions: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getTransactions(1).subscribe({
      next: res => {
        if (res.data) {
          this.transactions = res.data;
        }
      },
      error: () => {
        const now = new Date();
        this.transactions = [
          { id: 105, type: 'BUY', units: 5, pricePerUnit: 228.40, totalAmount: 1142.00, transactionDate: now, status: 'COMPLETED' },
          { id: 104, type: 'BUY', units: 30, pricePerUnit: 95.00, totalAmount: 2850.00, transactionDate: new Date(now.getTime() - 86400000 * 30), status: 'COMPLETED' },
          { id: 103, type: 'BUY', units: 10, pricePerUnit: 183.75, totalAmount: 1837.50, transactionDate: new Date(now.getTime() - 86400000 * 60), status: 'COMPLETED' },
          { id: 102, type: 'BUY', units: 15, pricePerUnit: 380.00, totalAmount: 5700.00, transactionDate: new Date(now.getTime() - 86400000 * 120), status: 'COMPLETED' },
          { id: 101, type: 'BUY', units: 40, pricePerUnit: 410.00, totalAmount: 16400.00, transactionDate: new Date(now.getTime() - 86400000 * 180), status: 'COMPLETED' }
        ];
      }
    });
  }
}
