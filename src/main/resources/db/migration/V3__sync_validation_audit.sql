-- V3_fix_ticket_validation_audit.sql
-- Context: ticket_validation originally had created_at/updated_at, now removing them

-- 1. Add validated_at column (nullable for now)
ALTER TABLE ticket_validation
ADD COLUMN IF NOT EXISTS validated_at TIMESTAMPTZ;

-- 2. Backfill from existing created_at (IF IT EXISTS in V1)
UPDATE ticket_validation
SET validated_at = created_at
WHERE validated_at IS NULL;

-- 3. Enforce NOT NULL constraint
ALTER TABLE ticket_validation
ALTER COLUMN validated_at SET NOT NULL;

-- 4. Add index for validated_by FK
CREATE INDEX IF NOT EXISTS idx_ticket_validation_staff_id
ON ticket_validation (validated_by);

-- 5. Drop obsolete columns
ALTER TABLE ticket_validation DROP COLUMN IF EXISTS created_at;
ALTER TABLE ticket_validation DROP COLUMN IF EXISTS updated_at;