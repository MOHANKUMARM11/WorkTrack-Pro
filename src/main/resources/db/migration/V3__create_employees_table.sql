CREATE TABLE employees (
                           id BIGSERIAL PRIMARY KEY,

                           first_name VARCHAR(255) NOT NULL,
                           last_name VARCHAR(255) NOT NULL,

                           email VARCHAR(255) NOT NULL UNIQUE,
                           phone VARCHAR(20) NOT NULL,
                           password VARCHAR(255) NOT NULL,

                           role VARCHAR(50) NOT NULL,
                           status VARCHAR(50) NOT NULL,

                           company_id BIGINT NOT NULL,

                           CONSTRAINT fk_employee_company
                               FOREIGN KEY (company_id)
                                   REFERENCES companies(id)
                                   ON DELETE CASCADE
);