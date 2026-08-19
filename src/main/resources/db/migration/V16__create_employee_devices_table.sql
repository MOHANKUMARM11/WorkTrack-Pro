CREATE TABLE employee_devices (
                                  id BIGSERIAL PRIMARY KEY,

                                  employee_id BIGINT NOT NULL,

                                  device_id VARCHAR(255) NOT NULL,

                                  secret_hash VARCHAR(255) NOT NULL,

                                  active BOOLEAN NOT NULL DEFAULT TRUE,

                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  revoked_at TIMESTAMP NULL,

                                  CONSTRAINT fk_employee_device_employee
                                      FOREIGN KEY (employee_id)
                                          REFERENCES employees(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT uk_employee_device
                                      UNIQUE (employee_id, device_id)
);

CREATE INDEX idx_employee_devices_employee
    ON employee_devices(employee_id);

CREATE INDEX idx_employee_devices_active
    ON employee_devices(employee_id, active);