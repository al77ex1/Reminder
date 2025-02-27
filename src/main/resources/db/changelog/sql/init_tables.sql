-- encoding: utf8
CREATE TABLE notification (
    id              SERIAL PRIMARY KEY,
    job_name        VARCHAR(50) NOT NULL,
    trigger_name    VARCHAR(50) NOT NULL,
    cron_schedule   VARCHAR(20) NOT NULL,
    message TEXT    NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE
);
