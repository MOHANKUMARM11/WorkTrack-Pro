CREATE TABLE users (

                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                       first_name VARCHAR(100) NOT NULL,

                       last_name VARCHAR(100) NOT NULL,

                       email VARCHAR(150) NOT NULL UNIQUE,

                       password VARCHAR(255) NOT NULL,

                       phone VARCHAR(20) NOT NULL,

                       role VARCHAR(30) NOT NULL,

                       company_id BIGINT,

                       enabled BOOLEAN NOT NULL DEFAULT TRUE,

                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                       updated_at TIMESTAMPTZ,

                       created_by BIGINT,

                       updated_by BIGINT,

                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

                       CONSTRAINT fk_user_company
                           FOREIGN KEY (company_id)
                               REFERENCES companies(id)
);