CREATE TABLE resources (
                           id BIGSERIAL PRIMARY KEY,

                           name VARCHAR(150) NOT NULL,

                           description VARCHAR(1000),

                           type VARCHAR(100) NOT NULL,

                           quantity INTEGER NOT NULL,

                           status VARCHAR(20) NOT NULL,

                           company_id BIGINT NOT NULL,

                           employee_id BIGINT,

                           CONSTRAINT fk_resources_company
                               FOREIGN KEY (company_id)
                                   REFERENCES companies(id),

                           CONSTRAINT fk_resources_employee
                               FOREIGN KEY (employee_id)
                                   REFERENCES employees(id),

                           CONSTRAINT uk_resource_name_company
                               UNIQUE (name, company_id)
);