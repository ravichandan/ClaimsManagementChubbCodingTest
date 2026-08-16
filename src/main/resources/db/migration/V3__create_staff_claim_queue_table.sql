CREATE TABLE IF NOT EXISTS staff_claim_queue (
    id UUID PRIMARY KEY,
    claim_id UUID NOT NULL UNIQUE,
    staff_id UUID,
    status VARCHAR(50) NOT NULL,
    queued_at TIMESTAMP NOT NULL,
    picked_up_at TIMESTAMP,
    FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_staff_claim_queue_status ON staff_claim_queue(status);
CREATE INDEX IF NOT EXISTS idx_staff_claim_queue_staff_id ON staff_claim_queue(staff_id);
CREATE INDEX IF NOT EXISTS idx_staff_claim_queue_queued_at ON staff_claim_queue(queued_at);