-- ==============================================================================
-- InvestFlow User Service - Schema Initialization (V1)
-- ==============================================================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'roles')
BEGIN
    CREATE TABLE roles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name VARCHAR(50) NOT NULL UNIQUE,
        description VARCHAR(255) NULL
    );

    INSERT INTO roles (name, description) VALUES ('ROLE_USER', 'Standard investor user');
    INSERT INTO roles (name, description) VALUES ('ROLE_ADMIN', 'Platform administrator');
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    CREATE TABLE users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        email VARCHAR(150) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        first_name VARCHAR(100) NOT NULL,
        last_name VARCHAR(100) NOT NULL,
        phone VARCHAR(30) NULL,
        status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

    CREATE INDEX idx_users_email ON users(email);
    CREATE INDEX idx_users_status ON users(status);
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_roles')
BEGIN
    CREATE TABLE user_roles (
        user_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        PRIMARY KEY (user_id, role_id),
        CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'refresh_tokens')
BEGIN
    CREATE TABLE refresh_tokens (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        token VARCHAR(255) NOT NULL UNIQUE,
        expiry_date DATETIME2 NOT NULL,
        revoked BIT NOT NULL DEFAULT 0,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
    CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
END
GO
