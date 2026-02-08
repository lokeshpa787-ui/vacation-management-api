INSERT INTO EMPLOYEES (
    ID,
    NAME,
    ROLE,
    TOTAL_VACATION_DAYS_PER_YEAR,
    HIRED_AT
) VALUES
-- Workers
(
    '11111111-1111-1111-1111-111111111111',
    'Alice',
    'ROLE_WORKER',
    30,
    DATE '2022-01-10'
),
(
    '22222222-2222-2222-2222-222222222222',
    'Bob',
    'ROLE_WORKER',
    30,
    DATE '2023-03-15'
),
(
    '44444444-4444-4444-4444-444444444444',
    'David',
    'ROLE_WORKER',
    25,
    DATE '2024-02-01'
),
(
    '55555555-5555-5555-5555-555555555555',
    'Eva',
    'ROLE_WORKER',
    30,
    DATE '2021-09-20'
),

-- Managers
(
    '33333333-3333-3333-3333-333333333333',
    'Carol',
    'ROLE_MANAGER',
    30,
    DATE '2021-06-01'
),
(
    '66666666-6666-6666-6666-666666666666',
    'Frank',
    'ROLE_MANAGER',
    30,
    DATE '2020-11-05'
);




INSERT INTO VACATION_REQUESTS (
    ID,
    AUTHOR_ID,
    STATUS,
    VACATION_START_DATE,
    VACATION_END_DATE,
    REQUEST_CREATED_AT,
    RESOLVED_AT,
    RESOLVED_BY,
    COMMENT
) VALUES

-- Alice: Approved vacation
(
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '11111111-1111-1111-1111-111111111111',
    'APPROVED',
    DATE '2026-12-20',
    DATE '2026-12-24',
    TIMESTAMP '2026-10-01 10:00:00',
    TIMESTAMP '2026-10-02 09:00:00',
    '33333333-3333-3333-3333-333333333333',
    'Family trip'
),

-- Bob: Pending vacation (overlaps with Alice)
(
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '22222222-2222-2222-2222-222222222222',
    'PENDING',
    DATE '2026-12-22',
    DATE '2026-12-26',
    TIMESTAMP '2026-10-03 11:30:00',
    NULL,
    NULL,
    'Christmas travel'
),

-- David: Rejected vacation
(
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '44444444-4444-4444-4444-444444444444',
    'REJECTED',
    DATE '2026-11-10',
    DATE '2026-11-15',
    TIMESTAMP '2026-09-20 14:00:00',
    TIMESTAMP '2026-09-21 09:30:00',
    '33333333-3333-3333-3333-333333333333',
    'Project deadline'
),

-- Eva: Approved vacation (earlier in year)
(
    'dddddddd-dddd-dddd-dddd-dddddddddddd',
    '55555555-5555-5555-5555-555555555555',
    'APPROVED',
    DATE '2026-06-05',
    DATE '2026-06-10',
    TIMESTAMP '2026-04-01 08:45:00',
    TIMESTAMP '2026-04-02 10:00:00',
    '66666666-6666-6666-6666-666666666666',
    'Summer break'
);
