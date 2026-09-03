import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, SidebarComponent],
  template: `
    <div class="app-layout">
      <app-navbar *ngIf="isLoggedIn"></app-navbar>

      <div class="main-body" [class.no-auth]="!isLoggedIn">
        <app-sidebar *ngIf="isLoggedIn"></app-sidebar>
        <main class="content-area">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
  styles: [`
    .app-layout {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
      background: radial-gradient(circle at 10% 10%, #111827 0%, #080c14 100%);
    }
    .main-body {
      display: flex;
      flex: 1;
      width: 100%;
    }
    .main-body.no-auth {
      display: block;
    }
    .content-area {
      flex: 1;
      overflow-y: auto;
      min-width: 0;
    }
  `]
})
export class AppComponent implements OnInit {
  isLoggedIn = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
    });
    // Check if initial token is present
    if (this.api.isAuthenticated()) {
      this.isLoggedIn = true;
    }
  }
}
