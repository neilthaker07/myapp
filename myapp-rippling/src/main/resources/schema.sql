CREATE TABLE provider (
    provider_id       BIGINT PRIMARY KEY,
    name              VARCHAR(100)  NOT NULL,
    zip               INTEGER,
    late_appointments INTEGER       DEFAULT 0,
    client_retention  DECIMAL(4,2),
    age               INTEGER
);

-- recurring weekly schedule windows; one row per (provider, day, time block)
-- start_time/end_time are TIME (no date) — this is a repeating weekly template, not a concrete event
CREATE TABLE provider_schedule (
    schedule_id BIGINT      PRIMARY KEY,
    provider_id BIGINT      NOT NULL REFERENCES provider(provider_id),
    day_of_week VARCHAR(10) NOT NULL CHECK (day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    CHECK (start_time < end_time)
);

CREATE TABLE client (
    client_id             BIGINT       PRIMARY KEY,
    name                  VARCHAR(100) NOT NULL,
    preferred_provider_age INTEGER
);

-- one row per preferred day; mirrors provider_availability pattern
CREATE TABLE client_preferred_day (
    client_id   BIGINT      NOT NULL REFERENCES client(client_id),
    day_of_week VARCHAR(10) NOT NULL CHECK (day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
    PRIMARY KEY (client_id, day_of_week)
);

CREATE TABLE appointment (
    appointment_id BIGINT      PRIMARY KEY,
    start_time     TIMESTAMPTZ NOT NULL,
    end_time       TIMESTAMPTZ NOT NULL,
    status         VARCHAR(20) NOT NULL CHECK (status IN ('SCHEDULED','COMPLETED','CANCELLED')),
    provider_id    BIGINT      NOT NULL REFERENCES provider(provider_id),
    client_id      BIGINT      NOT NULL REFERENCES client(client_id),
    CONSTRAINT uq_provider_slot UNIQUE (provider_id, start_time)
);

CREATE INDEX idx_appointment_provider      ON appointment(provider_id);
CREATE INDEX idx_appointment_client        ON appointment(client_id);
CREATE INDEX idx_provider_schedule         ON provider_schedule(provider_id, day_of_week);
CREATE INDEX idx_client_preferred_day      ON client_preferred_day(client_id);
