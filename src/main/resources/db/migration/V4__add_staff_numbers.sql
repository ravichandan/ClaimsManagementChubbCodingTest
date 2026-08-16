ALTER TABLE staff ADD COLUMN staff_number VARCHAR(100);

UPDATE staff
SET staff_number = CASE
    WHEN id = '33333333-3333-3333-3333-333333333333' THEN 'STAFF-1001'
    WHEN id = '44444444-4444-4444-4444-444444444444' THEN 'STAFF-1002'
    ELSE 'STAFF-' || REPLACE(CAST(id AS VARCHAR), '-', '')
END
WHERE staff_number IS NULL;

ALTER TABLE staff ALTER COLUMN staff_number SET NOT NULL;
ALTER TABLE staff ADD CONSTRAINT uk_staff_staff_number UNIQUE (staff_number);
CREATE INDEX idx_staff_staff_number ON staff(staff_number);