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
    '77777777-7777-7777-7777-777777777777',
    'CLM-QUEUE1001',
    '22222222-2222-2222-2222-222222222222',
    'PROPERTY',
    'SUBMITTED',
    'Water damage to kitchen from burst pipe',
    TIMESTAMP '2024-01-16 09:00:00',
    TIMESTAMP '2024-01-16 09:00:00',
    0
);

INSERT INTO staff_claim_queue (
    id,
    claim_id,
    staff_id,
    status,
    queued_at,
    picked_up_at
)
VALUES (
    '88888888-8888-8888-8888-888888888888',
    '77777777-7777-7777-7777-777777777777',
    NULL,
    'AVAILABLE',
    TIMESTAMP '2024-01-16 09:00:00',
    NULL
);