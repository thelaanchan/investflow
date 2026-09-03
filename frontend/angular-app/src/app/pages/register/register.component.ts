import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="auth-container">
      <div class="auth-card glass-panel">
        <div class="auth-header">
          <div class="logo-box">IF</div>
          <h2>Join <span class="text-gradient">InvestFlow</span></h2>
          <p class="subtitle">Open an institutional investment account</p>
        </div>

        <div class="alert alert-error" *ngIf="errorMessage">
          {{ errorMessage }}
        </div>

        <form (ngSubmit)="onSubmit()">
          <div class="row">
            <div class="form-group">
              <label class="form-label">First Name</label>
              <input type="text" class="form-control" [(ngModel)]="firstName" name="firstName" required placeholder="Alex">
            </div>
            <div class="form-group">
              <label class="form-label">Last Name</label>
              <input type="text" class="form-control" [(ngModel)]="lastName" name="lastName" required placeholder="Mercer">
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Email Address</label>
            <input type="email" class="form-control" [(ngModel)]="email" name="email" required placeholder="alex@investflow.com">
          </div>

          <div class="form-group">
            <label class="form-label">Password</label>
            <input type="password" class="form-control" [(ngModel)]="password" name="password" required placeholder="Minimum 8 characters">
          </div>

          <button type="submit" class="btn btn-primary w-full" [disabled]="loading">
            <span *ngIf="!loading">Create Investment Account →</span>
            <span *ngIf="loading">Creating account...</span>
          </button>
        </form>

        <div class="auth-footer">
          Already have an account? <a routerLink="/login" class="link">Sign in</a>
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
      max-width: 480px;
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
    .row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 12px;
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
export class RegisterComponent {
  firstName = '';
  lastName = '';
  email = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(private api: ApiService, private router: Router) {}

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    this.api.register({
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      password: this.password
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Registration failed. Please check form details.';
      }
    });
  }
}
