CREATE TABLE companies (
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                           name VARCHAR(150) NOT NULL UNIQUE,

                           registration_number VARCHAR(50) UNIQUE,

                           industry VARCHAR(100),

                           subscription_plan VARCHAR(30) NOT NULL
                               CHECK (subscription_plan IN ('TRIAL','BASIC','PRO','ENTERPRISE')),

                           status VARCHAR(20) NOT NULL
                               CHECK (status IN ('ACTIVE','SUSPENDED','TRIAL_EXPIRED')),

                           timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',

                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                           updated_at TIMESTAMPTZ,

                           created_by BIGINT,

                           updated_by BIGINT,

                           is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);