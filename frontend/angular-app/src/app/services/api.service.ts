import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = 'http://localhost:8080/api';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const savedUser = localStorage.getItem('investflow_user');
    if (savedUser && savedUser !== 'undefined') {
      try {
        this.currentUserSubject.next(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('investflow_user');
      }
    }
  }

  getToken(): string | null {
    return localStorage.getItem('investflow_token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  login(credentials: any): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/auth/login`, credentials).pipe(
      tap(res => {
        if (res.data && res.data.accessToken) {
          const user: User = {
            id: res.data.userId,
            email: res.data.email,
            firstName: res.data.firstName,
            lastName: res.data.lastName,
            roles: res.data.roles
          };
          localStorage.setItem('investflow_token', res.data.accessToken);
          localStorage.setItem('investflow_refresh', res.data.refreshToken);
          localStorage.setItem('investflow_user', JSON.stringify(user));
          this.currentUserSubject.next(user);
        }
      })
    );
  }

  register(data: any): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/auth/register`, data).pipe(
      tap(res => {
        if (res.data && res.data.accessToken) {
          const user: User = {
            id: res.data.userId,
            email: res.data.email,
            firstName: res.data.firstName,
            lastName: res.data.lastName,
            roles: res.data.roles
          };
          localStorage.setItem('investflow_token', res.data.accessToken);
          localStorage.setItem('investflow_refresh', res.data.refreshToken);
          localStorage.setItem('investflow_user', JSON.stringify(user));
          this.currentUserSubject.next(user);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('investflow_token');
    localStorage.removeItem('investflow_refresh');
    localStorage.removeItem('investflow_user');
    this.currentUserSubject.next(null);
  }

  // Portfolios
  getPortfolios(): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.baseUrl}/portfolios`);
  }

  createPortfolio(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/portfolios`, data);
  }

  getPortfolio(id: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/portfolios/${id}`);
  }

  getHoldings(portfolioId: number): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.baseUrl}/portfolios/${portfolioId}/holdings`);
  }

  addHolding(portfolioId: number, data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/portfolios/${portfolioId}/holdings`, data);
  }

  deleteHolding(portfolioId: number, holdingId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/portfolios/${portfolioId}/holdings/${holdingId}`);
  }

  // Investments & Trades
  getInvestments(portfolioId?: number): Observable<ApiResponse<any[]>> {
    const url = portfolioId ? `${this.baseUrl}/investments?portfolioId=${portfolioId}` : `${this.baseUrl}/investments`;
    return this.http.get<ApiResponse<any[]>>(url);
  }

  createInvestment(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/investments`, data);
  }

  buyStock(investmentId: number, trade: { units: number; price: number }): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/investments/${investmentId}/buy`, trade);
  }

  sellStock(investmentId: number, trade: { units: number; price: number }): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/investments/${investmentId}/sell`, trade);
  }

  getTransactions(investmentId: number): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.baseUrl}/investments/${investmentId}/transactions`);
  }

  // SIPs
  getSips(portfolioId?: number): Observable<ApiResponse<any[]>> {
    const url = portfolioId ? `${this.baseUrl}/sips?portfolioId=${portfolioId}` : `${this.baseUrl}/sips`;
    return this.http.get<ApiResponse<any[]>>(url);
  }

  createSip(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/sips`, data);
  }

  updateSipStatus(id: number, status: string): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.baseUrl}/sips/${id}/status?status=${status}`, {});
  }

  executeSip(id: number): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/sips/${id}/execute`, {});
  }

  // Analytics
  getAnalytics(portfolioId: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/analytics/portfolio/${portfolioId}`);
  }

  getPerformance(portfolioId: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/analytics/portfolio/${portfolioId}/performance`);
  }

  getXRay(portfolioId: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/analytics/portfolio/${portfolioId}/xray`);
  }

  subscribeSseTicks(portfolioId: number): Observable<any> {
    return new Observable(observer => {
      const eventSource = new EventSource(`${this.baseUrl}/analytics/portfolio/${portfolioId}/events`);
      eventSource.addEventListener('portfolio-tick', (event: any) => {
        try {
          const data = JSON.parse(event.data);
          observer.next(data);
        } catch (e) {
          observer.next(event.data);
        }
      });
      eventSource.onerror = error => observer.error(error);
      return () => eventSource.close();
    });
  }

  // Notifications
  getNotifications(): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.baseUrl}/notifications`);
  }

  markNotificationAsRead(id: number): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.baseUrl}/notifications/${id}/read`, {});
  }

  markAllNotificationsAsRead(): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.baseUrl}/notifications/read-all`, {});
  }

  // AI Assistant
  askAi(question: string, portfolioId?: number): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/ai/query`, { question, portfolioId });
  }

  getAiSampleQuestions(): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(`${this.baseUrl}/ai/sample-questions`);
  }
}
