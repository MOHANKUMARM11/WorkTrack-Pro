-- Flyway Migration V20: Create Task Assignments Table for M06 Multi-Assignee Task Distribution

CREATE TABLE IF NOT EXISTS task_assignments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_assignments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_assignments_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT uk_task_assignments_task_employee UNIQUE (task_id, employee_id)
);

ALTER TABLE tasks ALTER COLUMN employee_id DROP NOT NULL;

CREATE INDEX IF NOT EXISTS idx_task_assignments_task ON task_assignments(task_id);
CREATE INDEX IF NOT EXISTS idx_task_assignments_employee ON task_assignments(employee_id);
