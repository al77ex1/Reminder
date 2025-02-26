-- Create notification table
CREATE TABLE IF NOT EXISTS notification (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    job_name VARCHAR(50) NOT NULL,
    trigger_name VARCHAR(50) NOT NULL,
    cron_schedule VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
