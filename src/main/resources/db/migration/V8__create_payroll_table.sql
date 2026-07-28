CREATE TABLE payrolls (

                          id BIGSERIAL PRIMARY KEY,

                          month INT NOT NULL,

                          year INT NOT NULL,

                          basic_salary NUMERIC(10,2) NOT NULL,

                          allowance NUMERIC(10,2) DEFAULT 0,

                          bonus NUMERIC(10,2) DEFAULT 0,

                          deduction NUMERIC(10,2) DEFAULT 0,

                          tax NUMERIC(10,2) DEFAULT 0,

                          net_salary NUMERIC(10,2) NOT NULL,

                          status VARCHAR(30),

                          employee_id BIGINT NOT NULL,

                          company_id BIGINT NOT NULL,

                          created_at TIMESTAMP,

                          updated_at TIMESTAMP,

                          CONSTRAINT fk_payroll_employee
                              FOREIGN KEY (employee_id)
                                  REFERENCES employees(id),

                          CONSTRAINT fk_payroll_company
                              FOREIGN KEY (company_id)
                                  REFERENCES companies(id)
);