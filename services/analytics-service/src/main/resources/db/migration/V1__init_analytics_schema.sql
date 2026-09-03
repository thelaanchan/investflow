-- ==============================================================================
-- InvestFlow Analytics Service - Schema Initialization (V1)
-- ==============================================================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'performance_snapshots')
BEGIN
    CREATE TABLE performance_snapshots (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        portfolio_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        snapshot_date DATE NOT NULL,
        total_invested DECIMAL(18, 2) NOT NULL,
        current_value DECIMAL(18, 2) NOT NULL,
        total_profit_loss DECIMAL(18, 2) NOT NULL,
        returns_percentage DECIMAL(18, 2) NOT NULL,
        xirr_rate DECIMAL(18, 4) NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

    CREATE INDEX idx_performance_snapshots_portfolio_id ON performance_snapshots(portfolio_id);
    CREATE INDEX idx_performance_snapshots_user_id ON performance_snapshots(user_id);
    CREATE INDEX idx_performance_snapshots_date ON performance_snapshots(snapshot_date);
END
GO
