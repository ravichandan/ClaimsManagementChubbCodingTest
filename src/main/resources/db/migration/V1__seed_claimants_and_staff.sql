-- Create claimants table
CREATE TABLE IF NOT EXISTS claimants (
    id UUID PRIMARY KEY,
    claimant_member_number VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address VARCHAR(500) NOT NULL,
    policy_number VARCHAR(100) NOT NULL
);

-- Create claims table
CREATE TABLE IF NOT EXISTS claims (
    id UUID PRIMARY KEY,
    claim_number VARCHAR(100) NOT NULL UNIQUE,
    claimant_id UUID NOT NULL,
    claim_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (claimant_id) REFERENCES claimants(id) ON DELETE CASCADE
);

-- Create staff table
CREATE TABLE IF NOT EXISTS staff (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    role VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_claims_claim_number ON claims(claim_number);
CREATE INDEX IF NOT EXISTS idx_claims_claimant_id ON claims(claimant_id);
CREATE INDEX IF NOT EXISTS idx_claimants_member_number ON claimants(claimant_member_number);

-- Seed claimants
INSERT INTO claimants (id, claimant_member_number, first_name, last_name, email, phone, address, policy_number)
VALUES ('11111111-1111-1111-1111-111111111111', 'CM-1001', 'John', 'Doe', 'john.doe@example.com', '+1-555-0100', '123 Main St, Sydney, NSW', 'POL-1001');

INSERT INTO claimants (id, claimant_member_number, first_name, last_name, email, phone, address, policy_number)
VALUES ('22222222-2222-2222-2222-222222222222', 'CM-1002', 'Jane', 'Smith', 'jane.smith@example.com', '+1-555-0101', '45 Harbour Ave, Melbourne, VIC', 'POL-1002');

-- Seed staff
INSERT INTO staff (id, first_name, last_name, email, phone, role, created_at)
VALUES ('33333333-3333-3333-3333-333333333333', 'Alicia', 'Nguyen', 'alicia.nguyen@claims.local', '+61-400-000-001', 'CLAIMS_ADJUSTER', CURRENT_TIMESTAMP);

INSERT INTO staff (id, first_name, last_name, email, phone, role, created_at)
VALUES ('44444444-4444-4444-4444-444444444444', 'Marcus', 'Lee', 'marcus.lee@claims.local', '+61-400-000-002', 'TEAM_LEAD', CURRENT_TIMESTAMP);
