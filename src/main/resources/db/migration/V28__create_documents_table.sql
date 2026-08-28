-- Flyway Migration V28: Create Documents Table for M18 Advanced Reporting & Document Management

CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    file_path VARCHAR(500),
    category VARCHAR(50) DEFAULT 'GENERAL',
    uploader_id BIGINT,
    company_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documents_uploader FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_documents_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_documents_company ON documents(company_id);
CREATE INDEX IF NOT EXISTS idx_documents_uploader ON documents(uploader_id);
