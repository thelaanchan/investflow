import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="auth-container">
      <div class="auth-card glass-panel">
        <div class="auth-header">
          <div class="logo-box">IF</div>
          <h2>Sign in to <span class="text-gradient">InvestFlow</span></h2>
          <p class="subtitle">Institutional-Grade Portfolio & Wealth Platform</p>
        </div>

        <div class="alert alert-error" *ngIf="errorMessage">
          {{ errorMessage }}
        </div>

        <form (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label class="form-label">Email Address</label>
            <input type="email" class="form-control" [(ngModel)]="email" name="email" required placeholder="alex.mercer@investflow.com">
          </div>

          <div class="form-group">
            <label class="form-label">Password</label>
            <input type="password" class="form-control" [(ngModel)]="password" name="password" required placeholder="••••••••">
          </div>

          <button type="submit" class="btn btn-primary w-full" [disabled]="loading">
            <span *ngIf="!loading">Authenticate & Access Dashboard →</span>
            <span *ngIf="loading">Verifying credentials...</span>
          </button>
        </form>

        <div class="demo-credentials">
          <span class="demo-title">Quick Demo Login:</span>
          <div class="demo-btns">
            <button type="button" class="btn-demo" (click)="fillUser()">Demo User (user&#64;investflow.com)</button>
            <button type="button" class="btn-demo" (click)="fillAdmin()">Admin (admin&#64;investflow.com)</button>
          </div>
        </div>

        <div class="auth-footer">
          Don't have an account? <a routerLink="/register" class="link">Create an account</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-container {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      padding: 20px;
    }
    .auth-card {
      width: 100%;
      max-width: 440px;
      padding: 36px;
    }
    .auth-header {
      text-align: center;
      margin-bottom: 26px;
    }
    .logo-box {
      width: 46px;
      height: 46px;
      margin: 0 auto 14px;
      border-radius: 12px;
      background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 50%, #8b5cf6 100%);
      color: #fff;
      font-weight: 800;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.3rem;
      box-shadow: 0 0 20px rgba(6, 182, 212, 0.4);
    }
    .auth-header h2 {
      font-size: 1.45rem;
      font-weight: 800;
      letter-spacing: -0.5px;
    }
    .subtitle {
      font-size: 0.85rem;
      color: #94a3b8;
      margin-top: 4px;
    }
    .alert-error {
      background: rgba(244, 63, 94, 0.15);
      border: 1px solid rgba(244, 63, 94, 0.3);
      color: #f43f5e;
      padding: 10px 14px;
      border-radius: 8px;
      font-size: 0.85rem;
      margin-bottom: 18px;
    }
    .w-full {
      width: 100%;
      margin-top: 8px;
    }
    .demo-credentials {
      margin-top: 24px;
      padding-top: 18px;
      border-top: 1px solid rgba(255, 255, 255, 0.08);
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .demo-title {
      font-size: 0.75rem;
      font-weight: 600;
      color: #64748b;
      text-transform: uppercase;
    }
    .demo-btns {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }
    .btn-demo {
      background: rgba(255, 255, 255, 0.04);
      border: 1px dashed rgba(255, 255, 255, 0.15);
      color: #94a3b8;
      font-size: 0.8rem;
      padding: 6px 10px;
      border-radius: 6px;
      cursor: pointer;
      text-align: left;
    }
    .btn-demo:hover {
      background: rgba(56, 189, 248, 0.1);
      border-color: #38bdf8;
      color: #38bdf8;
    }
    .auth-footer {
      text-align: center;
      margin-top: 20px;
      font-size: 0.85rem;
      color: #94a3b8;
    }
    .link {
      color: #38bdf8;
      text-decoration: none;
      font-weight: 600;
    }
  `]
})
export class LoginComponent {
  email = 'user@investflow.com';
  password = 'User@12345';
  loading = false;
  errorMessage = '';

  constructor(private api: ApiService, private router: Router) {}

  fillUser(): void {
    this.email = 'user@investflow.com';
    this.password = 'User@12345';
  }

  fillAdmin(): void {
    this.email = 'admin@investflow.com';
    this.password = 'Admin@12345';
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    this.api.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Authentication failed. Please check credentials.';
      }
    });
  }
}
