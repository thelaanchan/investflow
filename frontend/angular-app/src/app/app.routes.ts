import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { PortfoliosComponent } from './pages/portfolios/portfolios.component';
import { HoldingsComponent } from './pages/holdings/holdings.component';
import { InvestmentsComponent } from './pages/investments/investments.component';
import { SipsComponent } from './pages/sips/sips.component';
import { TransactionsComponent } from './pages/transactions/transactions.component';
import { AnalyticsComponent } from './pages/analytics/analytics.component';
import { XrayComponent } from './pages/xray/xray.component';
import { AiAssistantComponent } from './pages/ai-assistant/ai-assistant.component';
import { AdminComponent } from './pages/admin/admin.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'portfolios', component: PortfoliosComponent, canActivate: [authGuard] },
  { path: 'holdings', component: HoldingsComponent, canActivate: [authGuard] },
  { path: 'investments', component: InvestmentsComponent, canActivate: [authGuard] },
  { path: 'sips', component: SipsComponent, canActivate: [authGuard] },
  { path: 'transactions', component: TransactionsComponent, canActivate: [authGuard] },
  { path: 'analytics', component: AnalyticsComponent, canActivate: [authGuard] },
  { path: 'xray', component: XrayComponent, canActivate: [authGuard] },
  { path: 'ai-assistant', component: AiAssistantComponent, canActivate: [authGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];
