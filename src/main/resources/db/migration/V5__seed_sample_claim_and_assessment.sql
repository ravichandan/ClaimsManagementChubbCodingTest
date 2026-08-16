-- Seed a stable claim and assessment used by the API examples.
INSERT INTO claims (
    id,
    claim_number,
    claimant_id,
    claim_type,
    status,
    description,
    created_at,
    updated_at,
    version
)
VALUES (
    '55555555-5555-5555-5555-555555555555',
    'CLM-ABC12345',
    '11111111-1111-1111-1111-111111111111',
    'MOTOR',
    'APPROVED',
    'Car accident on Main Street involving two vehicles',
    TIMESTAMP '2024-01-15 10:30:00',
    TIMESTAMP '2024-01-15 12:15:00',
    1
);

INSERT INTO assessments (
    id,
    claim_id,
    staff_id,
    assessment_type,
    description,
    details,
    estimated_amount,
    settled_amount,
    result,
    created_at,
    updated_at,
    version
)
VALUES (
    '66666666-6666-6666-6666-666666666666',
    '55555555-5555-5555-5555-555555555555',
    '33333333-3333-3333-3333-333333333333',
    'INITIAL_REVIEW',
    'Initial assessment completed. Claim is valid and within policy terms.',
    'All documentation provided. No red flags identified.',
    5000.00,
    4800.00,
    'APPROVED',
    TIMESTAMP '2024-01-15 11:30:00',
    TIMESTAMP '2024-01-15 12:15:00',
    0
);