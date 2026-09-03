-- ==============================================================================
-- InvestFlow - Multi-Service Database Initialization Script
-- Microsoft SQL Server 2022
-- ==============================================================================

-- 1. Create Server Login for Application Services
IF NOT EXISTS (SELECT name FROM sys.server_principals WHERE name = 'investflow_app')
BEGIN
    DECLARE @password NVARCHAR(128) = N'$(DB_PASSWORD)';
    DECLARE @createLoginSql NVARCHAR(MAX) = N'CREATE LOGIN [investflow_app] WITH PASSWORD = ''' + REPLACE(@password, '''', '''''') + N''', CHECK_POLICY = OFF;';
    EXEC sp_executesql @createLoginSql;
    PRINT 'Created server login: investflow_app';
END
GO

-- 2. Create User Service Database
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'investflow_user')
BEGIN
    CREATE DATABASE [investflow_user];
    ALTER DATABASE [investflow_user] SET READ_COMMITTED_SNAPSHOT ON;
    PRINT 'Created database: investflow_user';
END
GO

USE [investflow_user];
GO
IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'investflow_app')
BEGIN
    CREATE USER [investflow_app] FOR LOGIN [investflow_app];
    ALTER ROLE db_owner ADD MEMBER [investflow_app];
    PRINT 'Added investflow_app user to investflow_user';
END
GO

-- 3. Create Portfolio Service Database
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'investflow_portfolio')
BEGIN
    CREATE DATABASE [investflow_portfolio];
    ALTER DATABASE [investflow_portfolio] SET READ_COMMITTED_SNAPSHOT ON;
    PRINT 'Created database: investflow_portfolio';
END
GO

USE [investflow_portfolio];
GO
IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'investflow_app')
BEGIN
    CREATE USER [investflow_app] FOR LOGIN [investflow_app];
    ALTER ROLE db_owner ADD MEMBER [investflow_app];
    PRINT 'Added investflow_app user to investflow_portfolio';
END
GO

-- 4. Create Investment Service Database
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'investflow_investment')
BEGIN
    CREATE DATABASE [investflow_investment];
    ALTER DATABASE [investflow_investment] SET READ_COMMITTED_SNAPSHOT ON;
    PRINT 'Created database: investflow_investment';
END
GO

USE [investflow_investment];
GO
IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'investflow_app')
BEGIN
    CREATE USER [investflow_app] FOR LOGIN [investflow_app];
    ALTER ROLE db_owner ADD MEMBER [investflow_app];
    PRINT 'Added investflow_app user to investflow_investment';
END
GO

-- 5. Create Analytics Service Database
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'investflow_analytics')
BEGIN
    CREATE DATABASE [investflow_analytics];
    ALTER DATABASE [investflow_analytics] SET READ_COMMITTED_SNAPSHOT ON;
    PRINT 'Created database: investflow_analytics';
END
GO

USE [investflow_analytics];
GO
IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'investflow_app')
BEGIN
    CREATE USER [investflow_app] FOR LOGIN [investflow_app];
    ALTER ROLE db_owner ADD MEMBER [investflow_app];
    PRINT 'Added investflow_app user to investflow_analytics';
END
GO

-- 6. Create Notification Service Database
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'investflow_notification')
BEGIN
    CREATE DATABASE [investflow_notification];
    ALTER DATABASE [investflow_notification] SET READ_COMMITTED_SNAPSHOT ON;
    PRINT 'Created database: investflow_notification';
END
GO

USE [investflow_notification];
GO
IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'investflow_app')
BEGIN
    CREATE USER [investflow_app] FOR LOGIN [investflow_app];
    ALTER ROLE db_owner ADD MEMBER [investflow_app];
    PRINT 'Added investflow_app user to investflow_notification';
END
GO

PRINT 'InvestFlow database initialization completed successfully.';
