-- 1) add column as nullable (no default)
ALTER TABLE service
  ADD COLUMN enabled BOOLEAN;

-- 2) populate existing rows with true
UPDATE service
SET enabled = true
WHERE enabled IS NULL;

-- 3) set default for new rows to true
ALTER TABLE service
  ALTER COLUMN enabled SET DEFAULT true;

-- 4) make column NOT NULL (only after we've populated values)
ALTER TABLE service
  ALTER COLUMN enabled SET NOT NULL;

