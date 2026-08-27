-- Flyway Migration V23: Create System Settings Table for M13 System Settings & Configuration

CREATE TABLE IF NOT EXISTS system_settings (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    category VARCHAR(50) DEFAULT 'GENERAL',
    description VARCHAR(255),
    company_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_system_settings_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT uk_system_settings_company_key UNIQUE (company_id, key)
);

CREATE INDEX IF NOT EXISTS idx_system_settings_company ON system_settings(company_id);
CREATE INDEX IF NOT EXISTS idx_system_settings_key ON system_settings(key);
