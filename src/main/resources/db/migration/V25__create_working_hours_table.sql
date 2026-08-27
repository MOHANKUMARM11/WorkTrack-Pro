-- Flyway Migration V25: Create Working Hours Table for M16 Holiday & Working Hours Calendar

CREATE TABLE IF NOT EXISTS working_hours (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL DEFAULT '09:00:00',
    end_time TIME NOT NULL DEFAULT '17:00:00',
    is_working_day BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_working_hours_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT uk_working_hours_company_day UNIQUE (company_id, day_of_week)
);

CREATE INDEX IF NOT EXISTS idx_working_hours_company ON working_hours(company_id);
