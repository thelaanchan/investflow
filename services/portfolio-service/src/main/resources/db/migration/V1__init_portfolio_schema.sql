-- ==============================================================================
-- InvestFlow Portfolio Service - Schema Initialization (V1)
-- ==============================================================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'portfolios')
BEGIN
    CREATE TABLE portfolios (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        name VARCHAR(150) NOT NULL,
        description VARCHAR(500) NULL,
        type VARCHAR(50) NOT NULL DEFAULT 'BALANCED',
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

    CREATE INDEX idx_portfolios_user_id ON portfolios(user_id);
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'holdings')
BEGIN
    CREATE TABLE holdings (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        portfolio_id BIGINT NOT NULL,
        asset_symbol VARCHAR(50) NOT NULL,
        asset_name VARCHAR(150) NOT NULL,
        asset_type VARCHAR(50) NOT NULL DEFAULT 'EQUITY',
        quantity DECIMAL(18, 4) NOT NULL DEFAULT 0.0000,
        average_buy_price DECIMAL(18, 4) NOT NULL DEFAULT 0.0000,
        current_price DECIMAL(18, 4) NOT NULL DEFAULT 0.0000,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_holdings_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE
    );

    CREATE INDEX idx_holdings_portfolio_id ON holdings(portfolio_id);
    CREATE INDEX idx_holdings_symbol ON holdings(asset_symbol);
END
GO
