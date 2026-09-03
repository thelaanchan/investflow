import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ApiService, User } from '../../services/api.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="navbar glass-panel">
      <div class="brand">
        <div class="brand-logo">IF</div>
        <span class="brand-name">Invest<span class="highlight">Flow</span></span>
        <span class="badge-tag">FINTECH PRO</span>
      </div>

      <!-- Live Market Ticker -->
      <div class="market-ticker">
        <span class="pulse-indicator"></span>
        <span class="ticker-item">S&P 500 <strong class="positive">5,864.20 (+0.42%)</strong></span>
        <span class="ticker-sep">•</span>
        <span class="ticker-item">NASDAQ <strong class="positive">18,320.10 (+0.68%)</strong></span>
        <span class="ticker-sep">•</span>
        <span class="ticker-item">BTC/USD <strong class="positive">$88,410 (+1.25%)</strong></span>
      </div>

      <!-- Right profile & alerts -->
      <div class="nav-right" *ngIf="currentUser">
        <div class="notifications-btn" (click)="toggleNotifications()" title="Alerts & Notifications">
          <span class="bell-icon">🔔</span>
          <span class="notif-badge" *ngIf="unreadCount > 0">{{ unreadCount }}</span>

          <div class="notif-dropdown glass-panel" *ngIf="showNotifications">
            <div class="notif-header">
              <h4>Real-Time Alerts</h4>
              <button class="mark-read-btn" (click)="markAllRead($event)">Mark All Read</button>
            </div>
            <div class="notif-list">
              <div class="notif-item" *ngFor="let n of notifications" [class.unread]="!n.readStatus">
                <div class="notif-title">{{ n.title }}</div>
                <div class="notif-msg">{{ n.message }}</div>
                <div class="notif-time">{{ n.createdAt | date:'short' }}</div>
              </div>
              <div class="notif-empty" *ngIf="notifications.length === 0">
                No new alerts
              </div>
            </div>
          </div>
        </div>

        <div class="user-profile">
          <div class="avatar">{{ userInitial }}</div>
          <div class="user-meta">
            <span class="user-name">{{ currentUser.firstName }} {{ currentUser.lastName }}</span>
            <span class="user-role">{{ currentUser.roles[0] || 'INVESTOR' }}</span>
          </div>
          <button class="logout-btn" (click)="logout()" title="Sign Out">✕</button>
        </div>
      </div>
    </header>
  `,
  styles: [`
    .navbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 24px;
      margin: 12px 20px 0 20px;
      border-radius: 14px;
      z-index: 100;
      position: relative;
    }
    .brand {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .brand-logo {
      width: 36px;
      height: 36px;
      border-radius: 10px;
      background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 50%, #8b5cf6 100%);
      color: #fff;
      font-weight: 800;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.1rem;
      letter-spacing: -0.5px;
      box-shadow: 0 0 15px rgba(6, 182, 212, 0.4);
    }
    .brand-name {
      font-size: 1.25rem;
      font-weight: 800;
      letter-spacing: -0.5px;
      color: #f8fafc;
    }
    .highlight {
      color: #38bdf8;
    }
    .badge-tag {
      font-size: 0.65rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      background: rgba(56, 189, 248, 0.15);
      color: #38bdf8;
      border: 1px solid rgba(56, 189, 248, 0.3);
      padding: 2px 6px;
      border-radius: 4px;
    }
    .market-ticker {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 0.82rem;
      color: #94a3b8;
      background: rgba(15, 23, 42, 0.6);
      padding: 6px 14px;
      border-radius: 9999px;
      border: 1px solid rgba(255, 255, 255, 0.05);
    }
    .ticker-sep {
      color: #475569;
    }
    .nav-right {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    .notifications-btn {
      position: relative;
      cursor: pointer;
      padding: 6px;
      border-radius: 8px;
    }
    .bell-icon {
      font-size: 1.2rem;
    }
    .notif-badge {
      position: absolute;
      top: -2px;
      right: -2px;
      background: #f43f5e;
      color: #fff;
      font-size: 0.65rem;
      font-weight: 700;
      border-radius: 9999px;
      padding: 1px 5px;
      border: 2px solid #090d16;
    }
    .notif-dropdown {
      position: absolute;
      right: 0;
      top: 42px;
      width: 320px;
      padding: 14px;
      background: #0f172a;
      z-index: 200;
      box-shadow: 0 10px 30px rgba(0,0,0,0.8);
    }
    .notif-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid rgba(255,255,255,0.08);
      padding-bottom: 8px;
      margin-bottom: 8px;
    }
    .notif-header h4 {
      font-size: 0.9rem;
      color: #f8fafc;
    }
    .mark-read-btn {
      background: none;
      border: none;
      color: #38bdf8;
      font-size: 0.75rem;
      cursor: pointer;
    }
    .notif-list {
      max-height: 260px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .notif-item {
      padding: 8px 10px;
      border-radius: 8px;
      background: rgba(255,255,255,0.02);
      border-left: 3px solid transparent;
    }
    .notif-item.unread {
      border-left-color: #38bdf8;
      background: rgba(56, 189, 248, 0.05);
    }
    .notif-title {
      font-size: 0.82rem;
      font-weight: 600;
      color: #f8fafc;
    }
    .notif-msg {
      font-size: 0.78rem;
      color: #94a3b8;
      margin-top: 2px;
    }
    .notif-time {
      font-size: 0.68rem;
      color: #64748b;
      margin-top: 4px;
    }
    .notif-empty {
      text-align: center;
      padding: 20px 0;
      color: #64748b;
      font-size: 0.85rem;
    }
    .user-profile {
      display: flex;
      align-items: center;
      gap: 10px;
      padding-left: 12px;
      border-left: 1px solid rgba(255, 255, 255, 0.1);
    }
    .avatar {
      width: 34px;
      height: 34px;
      border-radius: 50%;
      background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
      color: #fff;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 0.85rem;
    }
    .user-meta {
      display: flex;
      flex-direction: column;
    }
    .user-name {
      font-size: 0.85rem;
      font-weight: 600;
    }
    .user-role {
      font-size: 0.7rem;
      color: #64748b;
    }
    .logout-btn {
      background: rgba(244, 63, 94, 0.1);
      border: 1px solid rgba(244, 63, 94, 0.2);
      color: #f43f5e;
      border-radius: 6px;
      width: 26px;
      height: 26px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-left: 4px;
    }
    .logout-btn:hover {
      background: #f43f5e;
      color: #fff;
    }
  `]
})
export class NavbarComponent implements OnInit {
  currentUser: User | null = null;
  notifications: any[] = [];
  unreadCount = 0;
  showNotifications = false;

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit(): void {
    this.api.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.loadNotifications();
      }
    });
  }

  get userInitial(): string {
    return this.currentUser && this.currentUser.firstName ? this.currentUser.firstName.charAt(0).toUpperCase() : 'U';
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.loadNotifications();
    }
  }

  loadNotifications(): void {
    this.api.getNotifications().subscribe({
      next: res => {
        if (res.data) {
          this.notifications = res.data;
          this.unreadCount = this.notifications.filter(n => !n.readStatus).length;
        }
      },
      error: () => {}
    });
  }

  markAllRead(event: Event): void {
    event.stopPropagation();
    this.api.markAllNotificationsAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.readStatus = true);
        this.unreadCount = 0;
      }
    });
  }

  logout(): void {
    this.api.logout();
    this.router.navigate(['/login']);
  }
}
