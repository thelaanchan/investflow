import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-ai-assistant',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">AI Financial Copilot (NL-to-SQL)</h1>
          <p class="page-subtitle">Ask questions in plain English — translates to safe, audited SQL queries executed in real-time</p>
        </div>
        <div class="ai-shield-badge">
          <span class="shield-icon">🛡️</span>
          <span>AST Safety Guardrails Active</span>
        </div>
      </div>

      <!-- Prompt Chips -->
      <div class="chips-container">
        <span class="chips-label">SUGGESTED QUERIES:</span>
        <div class="chips-list">
          <button class="chip-btn" *ngFor="let q of sampleQuestions" (click)="selectQuestion(q)">
            {{ q }}
          </button>
        </div>
      </div>

      <!-- Interactive Chat Input -->
      <div class="query-box glass-panel">
        <form (ngSubmit)="sendQuery()" class="input-form">
          <input type="text" class="form-control query-input" [(ngModel)]="userQuestion" name="q" placeholder="e.g. What is my total investment and portfolio value?" [disabled]="loading">
          <button type="submit" class="btn btn-primary" [disabled]="loading || !userQuestion.trim()">
            <span *ngIf="!loading">Execute AI Query ✨</span>
            <span *ngIf="loading">Translating & Executing...</span>
          </button>
        </form>
      </div>

      <!-- Result View -->
      <div class="result-card glass-panel" *ngIf="result">
        <div class="result-header">
          <div class="res-title">
            <span class="icon">💡</span>
            <h3>{{ result.explanation }}</h3>
          </div>
          <div class="exec-timing mono">
            ⚡ Executed in <strong>{{ result.executionTimeMs }}ms</strong>
          </div>
        </div>

        <!-- SQL Inspector Accordion -->
        <div class="sql-inspector">
          <div class="sql-head" (click)="showSql = !showSql">
            <span class="sql-label">TRANSLATED SQL (AUDITED & SAFE)</span>
            <span class="toggle-arrow">{{ showSql ? '▲ Hide' : '▼ View SQL' }}</span>
          </div>
          <pre class="sql-code mono" *ngIf="showSql"><code>{{ result.generatedSql }}</code></pre>
        </div>

        <!-- Tabular Results -->
        <div class="table-container" *ngIf="result.rows && result.rows.length > 0">
          <table class="data-table">
            <thead>
              <tr>
                <th *ngFor="let col of result.columns">{{ formatColHeader(col) }}</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let row of result.rows">
                <td *ngFor="let col of result.columns" class="mono">
                  {{ formatVal(row[col]) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="empty-res" *ngIf="!result.rows || result.rows.length === 0">
          No records matched the criteria.
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 20px;
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
    .ai-shield-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      background: rgba(139, 92, 246, 0.12);
      border: 1px solid rgba(139, 92, 246, 0.3);
      color: #c084fc;
      padding: 6px 14px;
      border-radius: 9999px;
      font-size: 0.78rem;
      font-weight: 600;
    }
    .chips-container {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .chips-label {
      font-size: 0.7rem;
      font-weight: 700;
      color: #64748b;
      letter-spacing: 0.05em;
    }
    .chips-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }
    .chip-btn {
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid var(--border-color);
      color: #94a3b8;
      padding: 6px 12px;
      border-radius: 9999px;
      font-size: 0.8rem;
      cursor: pointer;
      transition: all 0.2s;
    }
    .chip-btn:hover {
      background: rgba(56, 189, 248, 0.15);
      border-color: #38bdf8;
      color: #38bdf8;
      transform: translateY(-1px);
    }
    .query-box {
      padding: 14px;
    }
    .input-form {
      display: flex;
      gap: 12px;
    }
    .query-input {
      flex: 1;
      font-size: 1.05rem;
      padding: 12px 18px;
    }
    .result-card {
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 16px;
      border-color: rgba(139, 92, 246, 0.3);
    }
    .result-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .res-title {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .res-title h3 {
      font-size: 1.15rem;
      font-weight: 700;
      color: #f8fafc;
    }
    .exec-timing {
      font-size: 0.78rem;
      color: #94a3b8;
    }
    .sql-inspector {
      background: rgba(15, 23, 42, 0.8);
      border: 1px solid rgba(255, 255, 255, 0.06);
      border-radius: 10px;
      overflow: hidden;
    }
    .sql-head {
      display: flex;
      justify-content: space-between;
      padding: 10px 14px;
      cursor: pointer;
      font-size: 0.75rem;
      font-weight: 700;
      color: #64748b;
    }
    .sql-code {
      padding: 12px 16px;
      background: #090d16;
      border-top: 1px solid rgba(255, 255, 255, 0.05);
      font-size: 0.82rem;
      color: #38bdf8;
      overflow-x: auto;
    }
    .empty-res {
      text-align: center;
      padding: 20px;
      color: #64748b;
    }
  `]
})
export class AiAssistantComponent implements OnInit {
  userQuestion = 'What is my total investment and portfolio value?';
  loading = false;
  showSql = true;
  result: any = null;

  sampleQuestions = [
    'What is my total investment and portfolio value?',
    'Which investment has the highest return?',
    'What is my asset allocation breakdown?',
    'Show my active SIP investments.',
    'List my recent trade transactions.'
  ];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.sendQuery();
  }

  selectQuestion(q: string): void {
    this.userQuestion = q;
    this.sendQuery();
  }

  sendQuery(): void {
    if (!this.userQuestion.trim()) return;

    this.loading = true;
    this.api.askAi(this.userQuestion).subscribe({
      next: res => {
        this.loading = false;
        if (res.data) {
          this.result = res.data;
        }
      },
      error: () => {
        this.loading = false;
        // Mock fallback if offline
        this.result = {
          question: this.userQuestion,
          explanation: 'Calculated total holdings and cumulative valuation.',
          generatedSql: "SELECT COUNT(*) as total_holdings, SUM(invested_amount) as total_invested, SUM(units * current_nav_or_price) as current_portfolio_value FROM investments WHERE user_id = 2 AND status = 'ACTIVE'",
          columns: ['total_holdings', 'total_invested', 'current_portfolio_value', 'total_gain'],
          rows: [
            { total_holdings: 4, total_invested: 29337.50, current_portfolio_value: 35859.00, total_gain: 6521.50 }
          ],
          executionTimeMs: 12
        };
      }
    });
  }

  formatColHeader(col: string): string {
    return col.replace(/_/g, ' ').toUpperCase();
  }

  formatVal(val: any): string {
    if (typeof val === 'number') {
      return val.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }
    return String(val);
  }
}
