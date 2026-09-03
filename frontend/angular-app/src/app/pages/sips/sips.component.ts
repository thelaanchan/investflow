import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-sips',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Systematic Investment Plans (SIP)</h1>
          <p class="page-subtitle">Automate recurring dollar-cost averaging into index funds & blue-chip equities</p>
        </div>
        <button class="btn btn-primary" (click)="showNewSipModal = true">+ Schedule New SIP</button>
      </div>

      <div class="sips-grid">
        <div class="sip-card glass-panel" *ngFor="let s of sips">
          <div class="sip-header">
            <div>
              <span class="sip-sym">{{ s.symbol }}</span>
              <h3 class="sip-name">{{ s.name }}</h3>
            </div>
            <span class="sip-status" [class.paused]="s.status === 'PAUSED'">{{ s.status }}</span>
          </div>

          <div class="sip-details">
            <div class="sip-row">
              <span class="lbl">Installment Amount:</span>
              <strong class="mono val">&#36;{{ s.installmentAmount | number:'1.2-2' }}</strong>
            </div>
            <div class="sip-row">
              <span class="lbl">Frequency:</span>
              <span class="val">{{ s.frequency }} (Day {{ s.dayOfMonth }})</span>
            </div>
            <div class="sip-row">
              <span class="lbl">Next Installment Date:</span>
              <span class="val font-semibold text-gradient">{{ s.nextExecutionDate | date:'mediumDate' }}</span>
            </div>
            <div class="sip-row">
              <span class="lbl">Cumulative Invested:</span>
              <span class="mono val positive">&#36;{{ s.totalInvested | number:'1.2-2' }}</span>
            </div>
          </div>

          <div class="sip-actions">
            <button class="btn btn-secondary btn-sm" (click)="toggleStatus(s)">
              {{ s.status === 'ACTIVE' ? '⏸ Pause' : '▶ Resume' }}
            </button>
            <button class="btn btn-success btn-sm" (click)="executeNow(s)" title="Trigger immediate installment debit and buy order">
              ⚡ Execute Now
            </button>
          </div>
        </div>
      </div>

      <!-- New SIP Modal -->
      <div class="modal-backdrop" *ngIf="showNewSipModal">
        <div class="modal-card glass-panel">
          <div class="modal-header">
            <h3>Schedule Systematic Investment (SIP)</h3>
            <button class="close-btn" (click)="showNewSipModal = false">✕</button>
          </div>
          <form (ngSubmit)="submitNewSip()">
            <div class="form-group">
              <label class="form-label">Asset Ticker</label>
              <input type="text" class="form-control" [(ngModel)]="newSym" name="sym" required placeholder="e.g. VOO">
            </div>
            <div class="form-group">
              <label class="form-label">SIP Plan Name</label>
              <input type="text" class="form-control" [(ngModel)]="newName" name="name" required placeholder="e.g. S&P 500 Monthly Accumulation">
            </div>
            <div class="form-group">
              <label class="form-label">Installment Amount ($)</label>
              <input type="number" step="10" min="10" class="form-control" [(ngModel)]="newAmount" name="amount" required placeholder="500">
            </div>
            <div class="form-group">
              <label class="form-label">Frequency</label>
              <select class="form-control" [(ngModel)]="newFreq" name="freq">
                <option value="MONTHLY">Monthly</option>
                <option value="WEEKLY">Weekly</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Debit Day of Month (1 - 28)</label>
              <input type="number" min="1" max="28" class="form-control" [(ngModel)]="newDay" name="day" required>
            </div>
            <button type="submit" class="btn btn-primary w-full">Activate Recurring SIP →</button>
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
    .sips-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
      gap: 20px;
    }
    .sip-card {
      padding: 22px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .sip-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
    }
    .sip-sym {
      font-size: 0.8rem;
      font-weight: 800;
      color: #38bdf8;
      background: rgba(56, 189, 248, 0.15);
      padding: 2px 8px;
      border-radius: 4px;
    }
    .sip-name {
      font-size: 1.15rem;
      font-weight: 700;
      margin-top: 6px;
    }
    .sip-status {
      font-size: 0.72rem;
      font-weight: 700;
      color: #10b981;
      background: rgba(16, 185, 129, 0.12);
      padding: 3px 8px;
      border-radius: 9999px;
    }
    .sip-status.paused {
      color: #f59e0b;
      background: rgba(245, 158, 11, 0.12);
    }
    .sip-details {
      background: rgba(15, 23, 42, 0.5);
      padding: 14px;
      border-radius: 10px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .sip-row {
      display: flex;
      justify-content: space-between;
      font-size: 0.85rem;
    }
    .lbl {
      color: #94a3b8;
    }
    .font-semibold {
      font-weight: 600;
    }
    .sip-actions {
      display: flex;
      gap: 10px;
      margin-top: auto;
    }
    .btn-sm {
      padding: 7px 14px;
      font-size: 0.82rem;
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
    .w-full {
      width: 100%;
      margin-top: 10px;
    }
  `]
})
export class SipsComponent implements OnInit {
  sips: any[] = [];
  showNewSipModal = false;

  newSym = '';
  newName = '';
  newAmount = 500;
  newFreq = 'MONTHLY';
  newDay = 1;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadSips();
  }

  loadSips(): void {
    this.api.getSips().subscribe({
      next: res => {
        if (res.data) {
          this.sips = res.data;
        }
      },
      error: () => {
        this.sips = [
          { id: 1, symbol: 'VOO', name: 'Vanguard S&P 500 Monthly SIP', frequency: 'MONTHLY', installmentAmount: 500.00, dayOfMonth: 1, nextExecutionDate: '2026-10-01', status: 'ACTIVE', totalInvested: 4000.00 },
          { id: 2, symbol: 'MSFT', name: 'Microsoft Accumulation Plan', frequency: 'MONTHLY', installmentAmount: 250.00, dayOfMonth: 15, nextExecutionDate: '2026-10-15', status: 'ACTIVE', totalInvested: 1250.00 }
        ];
      }
    });
  }

  toggleStatus(s: any): void {
    const nextStatus = s.status === 'ACTIVE' ? 'PAUSED' : 'ACTIVE';
    this.api.updateSipStatus(s.id, nextStatus).subscribe({
      next: () => {
        s.status = nextStatus;
      }
    });
  }

  executeNow(s: any): void {
    this.api.executeSip(s.id).subscribe({
      next: () => {
        this.loadSips();
      }
    });
  }

  submitNewSip(): void {
    this.api.createSip({
      portfolioId: 1,
      symbol: this.newSym,
      name: this.newName,
      installmentAmount: this.newAmount,
      frequency: this.newFreq,
      dayOfMonth: this.newDay
    }).subscribe({
      next: () => {
        this.showNewSipModal = false;
        this.loadSips();
      }
    });
  }
}
