-- ==============================================================================
-- InvestFlow Investment Service - Schema Initialization (V1)
-- ==============================================================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'investments')
BEGIN
    CREATE TABLE investments (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        portfolio_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        symbol VARCHAR(50) NOT NULL,
        name VARCHAR(150) NOT NULL,
        asset_type VARCHAR(50) NOT NULL DEFAULT 'EQUITY', -- EQUITY, MUTUAL_FUND, BOND, CRYPTO
        units DECIMAL(18, 4) NOT NULL DEFAULT 0.0000,
        invested_amount DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
        current_nav_or_price DECIMAL(18, 4) NOT NULL DEFAULT 0.0000,
        status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

    CREATE INDEX idx_investments_user_id ON investments(user_id);
    CREATE INDEX idx_investments_portfolio_id ON investments(portfolio_id);
    CREATE INDEX idx_investments_symbol ON investments(symbol);
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'transactions')
BEGIN
    CREATE TABLE transactions (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        investment_id BIGINT NOT NULL,
        portfolio_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        type VARCHAR(30) NOT NULL, -- BUY, SELL, DIVIDEND
        units DECIMAL(18, 4) NOT NULL,
        price_per_unit DECIMAL(18, 4) NOT NULL,
        total_amount DECIMAL(18, 2) NOT NULL,
        transaction_date DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_transactions_investment FOREIGN KEY (investment_id) REFERENCES investments(id) ON DELETE CASCADE
    );

    CREATE INDEX idx_transactions_user_id ON transactions(user_id);
    CREATE INDEX idx_transactions_portfolio_id ON transactions(portfolio_id);
    CREATE INDEX idx_transactions_investment_id ON transactions(investment_id);
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'sips')
BEGIN
    CREATE TABLE sips (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        portfolio_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        symbol VARCHAR(50) NOT NULL,
        name VARCHAR(150) NOT NULL,
        frequency VARCHAR(30) NOT NULL DEFAULT 'MONTHLY', -- MONTHLY, WEEKLY
        installment_amount DECIMAL(18, 2) NOT NULL,
        day_of_month INT NOT NULL DEFAULT 1,
        next_execution_date DATE NOT NULL,
        status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PAUSED, CANCELLED
        total_invested DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

    CREATE INDEX idx_sips_user_id ON sips(user_id);
    CREATE INDEX idx_sips_portfolio_id ON sips(portfolio_id);
    CREATE INDEX idx_sips_status ON sips(status);
END
GO
