CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,

                       title VARCHAR(255) NOT NULL,
                       description TEXT,

                       priority VARCHAR(20) NOT NULL,
                       status VARCHAR(20) NOT NULL,

                       due_date DATE NOT NULL,

                       employee_id BIGINT NOT NULL,
                       company_id BIGINT NOT NULL,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_task_employee
                           FOREIGN KEY (employee_id)
                               REFERENCES employees(id)
                               ON DELETE CASCADE,

                       CONSTRAINT fk_task_company
                           FOREIGN KEY (company_id)
                               REFERENCES companies(id)
                               ON DELETE CASCADE
);