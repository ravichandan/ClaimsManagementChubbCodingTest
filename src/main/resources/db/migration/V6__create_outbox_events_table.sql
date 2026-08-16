CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(200) NOT NULL,
    destination VARCHAR(200) NOT NULL,
    payload CLOB NOT NULL,
    status VARCHAR(30) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP,
    last_error VARCHAR(2000)
);

CREATE INDEX IF NOT EXISTS idx_outbox_events_status_created_at
    ON outbox_events(status, created_at);