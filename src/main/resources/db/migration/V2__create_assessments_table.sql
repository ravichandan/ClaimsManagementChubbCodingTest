-- Create assessments table
CREATE TABLE IF NOT EXISTS assessments (
    id UUID PRIMARY KEY,
    claim_id UUID NOT NULL,
    staff_id UUID NOT NULL,
    assessment_type VARCHAR(500) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    details VARCHAR(2000),
    estimated_amount DOUBLE PRECISION NOT NULL,
    settled_amount DOUBLE PRECISION,
    result VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE RESTRICT
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_assessments_claim_id ON assessments(claim_id);
CREATE INDEX IF NOT EXISTS idx_assessments_staff_id ON assessments(staff_id);
CREATE INDEX IF NOT EXISTS idx_assessments_result ON assessments(result);
CREATE INDEX IF NOT EXISTS idx_assessments_created_at ON assessments(created_at);
