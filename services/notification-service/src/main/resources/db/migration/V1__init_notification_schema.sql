-- ==============================================================================
-- InvestFlow Notification Service - Schema Initialization (V1)
-- ==============================================================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'notifications')
BEGIN
    CREATE TABLE notifications (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        title VARCHAR(150) NOT NULL,
        message VARCHAR(1000) NOT NULL,
        type VARCHAR(50) NOT NULL DEFAULT 'PORTFOLIO_ALERT', -- PORTFOLIO_ALERT, TRANSACTION, SIP_REMINDER, MARKET_UPDATE
        read_status BIT NOT NULL DEFAULT 0,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

    CREATE INDEX idx_notifications_user_id ON notifications(user_id);
    CREATE INDEX idx_notifications_read ON notifications(read_status);
END
GO
