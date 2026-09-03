-- ==============================================================================
-- InvestFlow Comprehensive Bulk Data Seeding Script
-- ==============================================================================

USE investflow_user;
GO

-- Ensure demo users exist
IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'sarah.investor@investflow.com')
BEGIN
    DECLARE @hash VARCHAR(255) = (SELECT password_hash FROM users WHERE id = 2);
    INSERT INTO users (email, password_hash, first_name, last_name, phone, status, created_at, updated_at)
    VALUES ('sarah.investor@investflow.com', @hash, 'Sarah', 'Jenkins', '+1-555-0199', 'ACTIVE', SYSUTCDATETIME(), SYSUTCDATETIME());

    DECLARE @sarahId BIGINT = SCOPE_IDENTITY();
    DECLARE @userRoleId BIGINT = (SELECT id FROM roles WHERE name = 'ROLE_USER');
    INSERT INTO user_roles (user_id, role_id) VALUES (@sarahId, @userRoleId);
END
GO

-- ==============================================================================
-- Seed Portfolios & Holdings
-- ==============================================================================
USE investflow_portfolio;
GO

-- Delete old seed for user 2 to cleanly refresh
DELETE FROM holdings WHERE portfolio_id IN (SELECT id FROM portfolios WHERE user_id = 2);
DELETE FROM portfolios WHERE user_id = 2;

SET IDENTITY_INSERT portfolios ON;

INSERT INTO portfolios (id, user_id, name, description, type, created_at, updated_at) VALUES
(1, 2, 'Core Growth Wealth', 'High-conviction large cap tech and diversified index ETF portfolio', 'GROWTH', '2024-01-10', SYSUTCDATETIME()),
(2, 2, 'Retirement & 401(k) Index', 'Passive long-term wealth compounder tracking global indices and bonds', 'RETIREMENT', '2024-02-15', SYSUTCDATETIME()),
(3, 2, 'High-Yield Dividend & Value', 'Cash-flow generating blue chips with consistent dividend growth', 'BALANCED', '2024-04-01', SYSUTCDATETIME()),
(4, 2, 'Next-Gen AI & Tech Disruptors', 'High-beta growth exposure across AI, semiconductors, and cloud computing', 'AGGRESSIVE', '2024-06-20', SYSUTCDATETIME());

SET IDENTITY_INSERT portfolios OFF;

-- Insert Holdings for Portfolio 1 (Core Growth)
INSERT INTO holdings (portfolio_id, asset_symbol, asset_name, asset_type, quantity, average_buy_price, current_price, created_at, updated_at) VALUES
(1, 'AAPL', 'Apple Inc.', 'EQUITY', 25.0000, 175.5000, 228.4000, '2024-01-15', SYSUTCDATETIME()),
(1, 'MSFT', 'Microsoft Corporation', 'EQUITY', 15.0000, 380.0000, 445.2000, '2024-01-20', SYSUTCDATETIME()),
(1, 'VOO', 'Vanguard S&P 500 ETF', 'MUTUAL_FUND', 40.0000, 410.0000, 512.8000, '2024-02-01', SYSUTCDATETIME()),
(1, 'NVDA', 'NVIDIA Corporation', 'EQUITY', 30.0000, 95.0000, 132.5000, '2024-03-10', SYSUTCDATETIME()),
(1, 'GOOGL', 'Alphabet Inc. Class A', 'EQUITY', 20.0000, 142.0000, 178.6000, '2024-04-05', SYSUTCDATETIME()),
(1, 'AMZN', 'Amazon.com Inc.', 'EQUITY', 25.0000, 155.0000, 186.4000, '2024-05-12', SYSUTCDATETIME());

-- Insert Holdings for Portfolio 2 (Retirement)
INSERT INTO holdings (portfolio_id, asset_symbol, asset_name, asset_type, quantity, average_buy_price, current_price, created_at, updated_at) VALUES
(2, 'VTI', 'Vanguard Total Stock Market ETF', 'MUTUAL_FUND', 80.0000, 220.0000, 275.5000, '2024-02-20', SYSUTCDATETIME()),
(2, 'BND', 'Vanguard Total Bond Market ETF', 'BOND', 100.0000, 72.0000, 74.8000, '2024-02-25', SYSUTCDATETIME()),
(2, 'VXUS', 'Vanguard Total International Stock', 'MUTUAL_FUND', 60.0000, 54.0000, 62.1000, '2024-03-01', SYSUTCDATETIME());

-- Insert Holdings for Portfolio 3 (Dividends)
INSERT INTO holdings (portfolio_id, asset_symbol, asset_name, asset_type, quantity, average_buy_price, current_price, created_at, updated_at) VALUES
(3, 'SCHD', 'Schwab US Dividend Equity ETF', 'MUTUAL_FUND', 90.0000, 74.5000, 83.2000, '2024-04-10', SYSUTCDATETIME()),
(3, 'JNJ', 'Johnson & Johnson', 'EQUITY', 35.0000, 150.0000, 162.8000, '2024-04-15', SYSUTCDATETIME()),
(3, 'JPM', 'JPMorgan Chase & Co.', 'EQUITY', 25.0000, 185.0000, 220.5000, '2024-04-20', SYSUTCDATETIME()),
(3, 'PG', 'Procter & Gamble Co.', 'EQUITY', 30.0000, 152.0000, 172.1000, '2024-05-01', SYSUTCDATETIME());

-- Insert Holdings for Portfolio 4 (AI Tech)
INSERT INTO holdings (portfolio_id, asset_symbol, asset_name, asset_type, quantity, average_buy_price, current_price, created_at, updated_at) VALUES
(4, 'QQQ', 'Invesco QQQ Trust', 'MUTUAL_FUND', 45.0000, 420.0000, 485.6000, '2024-06-25', SYSUTCDATETIME()),
(4, 'META', 'Meta Platforms Inc.', 'EQUITY', 15.0000, 460.0000, 520.4000, '2024-07-01', SYSUTCDATETIME()),
(4, 'AMD', 'Advanced Micro Devices Inc.', 'EQUITY', 40.0000, 135.0000, 158.2000, '2024-07-15', SYSUTCDATETIME()),
(4, 'TSLA', 'Tesla Inc.', 'EQUITY', 20.0000, 195.0000, 235.1000, '2024-08-01', SYSUTCDATETIME());

GO

-- ==============================================================================
-- Seed Investments, Transactions & SIPs
-- ==============================================================================
USE investflow_investment;
GO

DELETE FROM transactions WHERE user_id = 2;
DELETE FROM sips WHERE user_id = 2;
DELETE FROM investments WHERE user_id = 2;

SET IDENTITY_INSERT investments ON;

INSERT INTO investments (id, portfolio_id, user_id, symbol, name, asset_type, units, invested_amount, current_nav_or_price, status, created_at, updated_at) VALUES
(1, 1, 2, 'AAPL', 'Apple Inc.', 'EQUITY', 25.0000, 4387.50, 228.4000, 'ACTIVE', '2024-01-15', SYSUTCDATETIME()),
(2, 1, 2, 'MSFT', 'Microsoft Corporation', 'EQUITY', 15.0000, 5700.00, 445.2000, 'ACTIVE', '2024-01-20', SYSUTCDATETIME()),
(3, 1, 2, 'VOO', 'Vanguard S&P 500 ETF', 'MUTUAL_FUND', 40.0000, 16400.00, 512.8000, 'ACTIVE', '2024-02-01', SYSUTCDATETIME()),
(4, 1, 2, 'NVDA', 'NVIDIA Corporation', 'EQUITY', 30.0000, 2850.00, 132.5000, 'ACTIVE', '2024-03-10', SYSUTCDATETIME()),
(5, 1, 2, 'GOOGL', 'Alphabet Inc. Class A', 'EQUITY', 20.0000, 2840.00, 178.6000, 'ACTIVE', '2024-04-05', SYSUTCDATETIME()),
(6, 1, 2, 'AMZN', 'Amazon.com Inc.', 'EQUITY', 25.0000, 3875.00, 186.4000, 'ACTIVE', '2024-05-12', SYSUTCDATETIME()),
(7, 2, 2, 'VTI', 'Vanguard Total Stock Market ETF', 'MUTUAL_FUND', 80.0000, 17600.00, 275.5000, 'ACTIVE', '2024-02-20', SYSUTCDATETIME()),
(8, 2, 2, 'BND', 'Vanguard Total Bond Market ETF', 'BOND', 100.0000, 7200.00, 74.8000, 'ACTIVE', '2024-02-25', SYSUTCDATETIME()),
(9, 3, 2, 'SCHD', 'Schwab US Dividend Equity ETF', 'MUTUAL_FUND', 90.0000, 6705.00, 83.2000, 'ACTIVE', '2024-04-10', SYSUTCDATETIME()),
(10, 3, 2, 'JNJ', 'Johnson & Johnson', 'EQUITY', 35.0000, 5250.00, 162.8000, 'ACTIVE', '2024-04-15', SYSUTCDATETIME()),
(11, 4, 2, 'QQQ', 'Invesco QQQ Trust', 'MUTUAL_FUND', 45.0000, 18900.00, 485.6000, 'ACTIVE', '2024-06-25', SYSUTCDATETIME()),
(12, 4, 2, 'META', 'Meta Platforms Inc.', 'EQUITY', 15.0000, 6900.00, 520.4000, 'ACTIVE', '2024-07-01', SYSUTCDATETIME());

SET IDENTITY_INSERT investments OFF;

-- Insert Extensive Historical Transactions (40+ Records across 2024 - 2026)
INSERT INTO transactions (investment_id, portfolio_id, user_id, type, units, price_per_unit, total_amount, transaction_date, status) VALUES
(1, 1, 2, 'BUY', 10.0000, 172.5000, 1725.00, '2024-01-15 09:30:00', 'COMPLETED'),
(1, 1, 2, 'BUY', 15.0000, 177.5000, 2662.50, '2024-02-14 14:15:00', 'COMPLETED'),
(1, 1, 2, 'DIVIDEND', 25.0000, 0.2400, 6.00, '2024-05-16 10:00:00', 'COMPLETED'),
(1, 1, 2, 'DIVIDEND', 25.0000, 0.2500, 6.25, '2024-08-15 10:00:00', 'COMPLETED'),
(2, 1, 2, 'BUY', 15.0000, 380.0000, 5700.00, '2024-01-20 10:45:00', 'COMPLETED'),
(2, 1, 2, 'DIVIDEND', 15.0000, 0.7500, 11.25, '2024-03-14 11:30:00', 'COMPLETED'),
(3, 1, 2, 'BUY', 20.0000, 405.0000, 8100.00, '2024-02-01 11:00:00', 'COMPLETED'),
(3, 1, 2, 'BUY', 20.0000, 415.0000, 8300.00, '2024-03-01 11:00:00', 'COMPLETED'),
(3, 1, 2, 'DIVIDEND', 40.0000, 1.7800, 71.20, '2024-03-28 09:30:00', 'COMPLETED'),
(3, 1, 2, 'DIVIDEND', 40.0000, 1.8200, 72.80, '2024-06-27 09:30:00', 'COMPLETED'),
(4, 1, 2, 'BUY', 30.0000, 95.0000, 2850.00, '2024-03-10 13:20:00', 'COMPLETED'),
(5, 1, 2, 'BUY', 20.0000, 142.0000, 2840.00, '2024-04-05 15:10:00', 'COMPLETED'),
(6, 1, 2, 'BUY', 25.0000, 155.0000, 3875.00, '2024-05-12 10:15:00', 'COMPLETED'),
(7, 2, 2, 'BUY', 40.0000, 218.0000, 8720.00, '2024-02-20 09:45:00', 'COMPLETED'),
(7, 2, 2, 'BUY', 40.0000, 222.0000, 8880.00, '2024-03-20 09:45:00', 'COMPLETED'),
(8, 2, 2, 'BUY', 100.0000, 72.0000, 7200.00, '2024-02-25 14:00:00', 'COMPLETED'),
(9, 3, 2, 'BUY', 50.0000, 74.0000, 3700.00, '2024-04-10 11:00:00', 'COMPLETED'),
(9, 3, 2, 'BUY', 40.0000, 75.1250, 3005.00, '2024-05-10 11:00:00', 'COMPLETED'),
(10, 3, 2, 'BUY', 35.0000, 150.0000, 5250.00, '2024-04-15 13:45:00', 'COMPLETED'),
(11, 4, 2, 'BUY', 25.0000, 415.0000, 10375.00, '2024-06-25 10:30:00', 'COMPLETED'),
(11, 4, 2, 'BUY', 20.0000, 426.2500, 8525.00, '2024-07-25 10:30:00', 'COMPLETED'),
(12, 4, 2, 'BUY', 15.0000, 460.0000, 6900.00, '2024-07-01 12:00:00', 'COMPLETED'),
(1, 1, 2, 'BUY', 5.0000, 215.0000, 1075.00, '2025-01-10 10:00:00', 'COMPLETED'),
(3, 1, 2, 'BUY', 5.0000, 480.0000, 2400.00, '2025-03-01 10:00:00', 'COMPLETED'),
(4, 1, 2, 'BUY', 10.0000, 120.0000, 1200.00, '2025-06-15 14:30:00', 'COMPLETED'),
(11, 4, 2, 'BUY', 5.0000, 465.0000, 2325.00, '2025-09-20 11:15:00', 'COMPLETED'),
(1, 1, 2, 'DIVIDEND', 25.0000, 0.2600, 6.50, '2026-02-12 10:00:00', 'COMPLETED'),
(3, 1, 2, 'DIVIDEND', 40.0000, 1.9200, 76.80, '2026-03-27 10:00:00', 'COMPLETED');

-- Insert 4 Systematic Investment Plans (SIP)
INSERT INTO sips (portfolio_id, user_id, symbol, name, frequency, installment_amount, day_of_month, next_execution_date, status, total_invested, created_at, updated_at) VALUES
(1, 2, 'VOO', 'Vanguard S&P 500 Monthly Accumulator', 'MONTHLY', 500.00, 1, '2026-10-01', 'ACTIVE', 12000.00, '2024-02-01', SYSUTCDATETIME()),
(1, 2, 'MSFT', 'Microsoft Long-Term Cloud & AI SIP', 'MONTHLY', 250.00, 15, '2026-10-15', 'ACTIVE', 6250.00, '2024-02-01', SYSUTCDATETIME()),
(4, 2, 'QQQ', 'Nasdaq 100 Growth Accumulator', 'MONTHLY', 350.00, 5, '2026-10-05', 'ACTIVE', 7700.00, '2024-06-25', SYSUTCDATETIME()),
(3, 2, 'SCHD', 'High Dividend Yield Compounding Plan', 'MONTHLY', 200.00, 20, '2026-10-20', 'PAUSED', 4200.00, '2024-04-10', SYSUTCDATETIME());

GO

-- ==============================================================================
-- Seed Historical Performance Snapshots
-- ==============================================================================
USE investflow_analytics;
GO

DELETE FROM performance_snapshots WHERE user_id = 2;

INSERT INTO performance_snapshots (portfolio_id, user_id, snapshot_date, total_invested, current_value, total_profit_loss, returns_percentage, xirr_rate, created_at) VALUES
(1, 2, '2025-01-01', 22000.00, 23500.00, 1500.00, 6.82, 0.1420, SYSUTCDATETIME()),
(1, 2, '2025-03-01', 25000.00, 27400.00, 2400.00, 9.60, 0.1650, SYSUTCDATETIME()),
(1, 2, '2025-06-01', 27000.00, 30800.00, 3800.00, 14.07, 0.1840, SYSUTCDATETIME()),
(1, 2, '2025-09-01', 28500.00, 33200.00, 4700.00, 16.49, 0.1980, SYSUTCDATETIME()),
(1, 2, '2025-12-01', 29000.00, 34900.00, 5900.00, 20.34, 0.2150, SYSUTCDATETIME()),
(1, 2, '2026-03-01', 29337.50, 36878.00, 7540.50, 25.70, 0.2285, SYSUTCDATETIME()),
(1, 2, '2026-06-01', 29337.50, 39420.00, 10082.50, 34.37, 0.2450, SYSUTCDATETIME()),
(1, 2, '2026-09-01', 29337.50, 42150.00, 12812.50, 43.67, 0.2568, SYSUTCDATETIME());

GO

-- ==============================================================================
-- Seed Real-time Notifications & Alerts
-- ==============================================================================
USE investflow_notification;
GO

DELETE FROM notifications WHERE user_id = 2;

INSERT INTO notifications (user_id, title, message, type, read_status, created_at) VALUES
(2, 'Dividend Credited: Vanguard S&P 500', 'Your quarterly dividend payment of $76.80 for 40 units of VOO has been credited to your account.', 'TRANSACTION', 0, DATEADD(hour, -2, SYSUTCDATETIME())),
(2, 'SIP Execution Completed: VOO', 'Systematic Investment Plan installment of $500.00 executed successfully at market price $512.80.', 'SIP_REMINDER', 0, DATEADD(day, -2, SYSUTCDATETIME())),
(2, 'All-Time High Alert: NVIDIA (NVDA)', 'NVIDIA Corp hit a new 52-week high of $132.50. Your position gain is currently +39.47%.', 'PORTFOLIO_ALERT', 0, DATEADD(day, -4, SYSUTCDATETIME())),
(2, 'Quarterly Rebalancing Recommendation', 'Technology exposure in Core Growth Wealth has reached 46.5%. Review portfolio X-Ray for diversification.', 'PORTFOLIO_ALERT', 1, DATEADD(day, -10, SYSUTCDATETIME())),
(2, 'Dividend Credited: Apple Inc.', 'Dividend payment of $6.50 for 25 units of AAPL has been processed.', 'TRANSACTION', 1, DATEADD(day, -20, SYSUTCDATETIME())),
(2, 'Security Login Detected', 'New login from Chrome macOS on IP 192.168.1.105 verified with valid JWT authentication.', 'PORTFOLIO_ALERT', 1, DATEADD(day, -30, SYSUTCDATETIME()));

GO
