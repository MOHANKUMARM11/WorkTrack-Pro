CREATE TABLE departments (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(100) NOT NULL,
                             company_id BIGINT NOT NULL,

                             CONSTRAINT fk_departments_company
                                 FOREIGN KEY (company_id)
                                     REFERENCES companies(id),

                             CONSTRAINT uk_departments_company_name
                                 UNIQUE (company_id, name)
);