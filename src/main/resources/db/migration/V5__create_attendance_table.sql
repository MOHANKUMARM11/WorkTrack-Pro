CREATE TABLE attendance (

                            id BIGSERIAL PRIMARY KEY,

                            employee_id BIGINT NOT NULL,

                            company_id BIGINT NOT NULL,

                            attendance_date DATE NOT NULL,

                            check_in TIME,

                            check_out TIME,

                            working_hours DOUBLE PRECISION,

                            status VARCHAR(30) NOT NULL,

                            created_at TIMESTAMP NOT NULL,

                            updated_at TIMESTAMP NOT NULL,

                            CONSTRAINT fk_attendance_employee
                                FOREIGN KEY (employee_id)
                                    REFERENCES employees(id),

                            CONSTRAINT fk_attendance_company
                                FOREIGN KEY (company_id)
                                    REFERENCES companies(id)
);