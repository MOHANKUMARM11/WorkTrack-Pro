CREATE TABLE leaves (

                        id BIGSERIAL PRIMARY KEY,

                        start_date DATE NOT NULL,

                        end_date DATE NOT NULL,

                        total_days INT NOT NULL,

                        reason VARCHAR(500),

                        leave_type VARCHAR(30),

                        status VARCHAR(30),

                        employee_id BIGINT NOT NULL,

                        company_id BIGINT NOT NULL,

                        created_at TIMESTAMP,

                        updated_at TIMESTAMP,

                        CONSTRAINT fk_leave_employee
                            FOREIGN KEY(employee_id)
                                REFERENCES employees(id),

                        CONSTRAINT fk_leave_company
                            FOREIGN KEY(company_id)
                                REFERENCES companies(id)
);