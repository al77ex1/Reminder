-- Create notification table
CREATE TABLE IF NOT EXISTS notification (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    message VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
