-- ============================================================
-- M04 - Attendance & GPS Check-in
-- V14
-- ============================================================

-- ------------------------------------------------------------
-- 1. Office locations
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS office_locations (
                                                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                company_id BIGINT NOT NULL,
                                                name VARCHAR(150) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    geofence_radius_m DOUBLE PRECISION NOT NULL DEFAULT 100,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_office_location_company
    FOREIGN KEY (company_id)
    REFERENCES companies(id),

    CONSTRAINT chk_office_latitude
    CHECK (latitude >= -90 AND latitude <= 90),

    CONSTRAINT chk_office_longitude
    CHECK (longitude >= -180 AND longitude <= 180),

    CONSTRAINT chk_office_geofence_radius
    CHECK (geofence_radius_m > 0)
    );

CREATE INDEX IF NOT EXISTS idx_office_locations_company
    ON office_locations(company_id);


-- ------------------------------------------------------------
-- 2. Employee → office location
-- ------------------------------------------------------------

ALTER TABLE employees
    ADD COLUMN IF NOT EXISTS office_location_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_employee_office_location'
    ) THEN
ALTER TABLE employees
    ADD CONSTRAINT fk_employee_office_location
        FOREIGN KEY (office_location_id)
            REFERENCES office_locations(id);
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_employee_office_location
    ON employees(office_location_id);


-- ------------------------------------------------------------
-- 3. Geofences
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS geofences (
                                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                         office_location_id BIGINT NOT NULL,
                                         radius_m DOUBLE PRECISION NOT NULL,
                                         beacon_ids TEXT[],
                                         wifi_bssids TEXT[],
                                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT fk_geofence_office
                                         FOREIGN KEY (office_location_id)
    REFERENCES office_locations(id)
    ON DELETE CASCADE,

    CONSTRAINT chk_geofence_radius
    CHECK (radius_m > 0),

    CONSTRAINT uq_geofence_office
    UNIQUE (office_location_id)
    );

CREATE INDEX IF NOT EXISTS idx_geofences_office
    ON geofences(office_location_id);


-- ------------------------------------------------------------
-- 4. Attendance logs
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS attendance_logs (
                                               id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                               attendance_id BIGINT NOT NULL,

                                               event_type VARCHAR(30) NOT NULL,

    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    accuracy_m DOUBLE PRECISION,

    source VARCHAR(20) NOT NULL,

    device_signature VARCHAR(512),

    beacon_id VARCHAR(255),
    wifi_bssid VARCHAR(255),

    manual_note TEXT,
    photo_url VARCHAR(1000),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attendance_log_attendance
    FOREIGN KEY (attendance_id)
    REFERENCES attendance(id)
    ON DELETE CASCADE,

    CONSTRAINT chk_attendance_log_event_type
    CHECK (
              event_type IN (
              'CHECK_IN',
              'CHECK_OUT',
              'BREAK_START',
              'BREAK_END',
              'CORRECTION'
                            )
    ),

    CONSTRAINT chk_attendance_log_source
    CHECK (
              source IN (
              'GPS',
              'BLE',
              'WIFI',
              'MANUAL'
                        )
    ),

    CONSTRAINT chk_attendance_log_latitude
    CHECK (
              latitude IS NULL
              OR (latitude >= -90 AND latitude <= 90)
    ),

    CONSTRAINT chk_attendance_log_longitude
    CHECK (
              longitude IS NULL
              OR (longitude >= -180 AND longitude <= 180)
    ),

    CONSTRAINT chk_attendance_log_accuracy
    CHECK (
              accuracy_m IS NULL OR accuracy_m >= 0
          )
    );

CREATE INDEX IF NOT EXISTS idx_attendance_logs_attendance
    ON attendance_logs(attendance_id);

CREATE INDEX IF NOT EXISTS idx_attendance_logs_event
    ON attendance_logs(event_type);


-- ------------------------------------------------------------
-- 5. GPS location history
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS gps_locations (
                                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                             attendance_log_id BIGINT NOT NULL,

                                             latitude DOUBLE PRECISION NOT NULL,
                                             longitude DOUBLE PRECISION NOT NULL,

                                             recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                             CONSTRAINT fk_gps_location_log
                                             FOREIGN KEY (attendance_log_id)
    REFERENCES attendance_logs(id)
    ON DELETE CASCADE,

    CONSTRAINT chk_gps_latitude
    CHECK (latitude >= -90 AND latitude <= 90),

    CONSTRAINT chk_gps_longitude
    CHECK (longitude >= -180 AND longitude <= 180)
    );

CREATE INDEX IF NOT EXISTS idx_gps_locations_log
    ON gps_locations(attendance_log_id);


-- ------------------------------------------------------------
-- 6. Breaks
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS breaks (
                                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      attendance_id BIGINT NOT NULL,

                                      start_at TIMESTAMP NOT NULL,
                                      end_at TIMESTAMP,
                                      duration_minutes INTEGER,

                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      CONSTRAINT fk_break_attendance
                                      FOREIGN KEY (attendance_id)
    REFERENCES attendance(id)
    ON DELETE CASCADE,

    CONSTRAINT chk_break_end
    CHECK (
              end_at IS NULL OR end_at >= start_at
          ),

    CONSTRAINT chk_break_duration
    CHECK (
              duration_minutes IS NULL OR duration_minutes >= 0
          )
    );

CREATE INDEX IF NOT EXISTS idx_breaks_attendance
    ON breaks(attendance_id);


-- ------------------------------------------------------------
-- 7. Attendance indexes
-- ------------------------------------------------------------

CREATE INDEX IF NOT EXISTS idx_attendance_employee_date
    ON attendance(employee_id, attendance_date);

CREATE INDEX IF NOT EXISTS idx_attendance_company_date
    ON attendance(company_id, attendance_date);

CREATE INDEX IF NOT EXISTS idx_attendance_status
    ON attendance(status);