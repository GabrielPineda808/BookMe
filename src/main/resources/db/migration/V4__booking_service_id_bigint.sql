-- Drop any existing FK from booking -> service (name-safe)
DO $$
DECLARE fk text;
BEGIN
  SELECT conname INTO fk
  FROM pg_constraint
  WHERE conrelid = 'booking'::regclass AND contype = 'f';
  IF fk IS NOT NULL THEN
    EXECUTE format('ALTER TABLE booking DROP CONSTRAINT %I', fk);
  END IF;
END $$;

-- Change column type INT -> BIGINT
ALTER TABLE booking
  ALTER COLUMN service_id TYPE BIGINT USING service_id::BIGINT;

-- Recreate FK (let Postgres target the PK of service)
ALTER TABLE booking
  ADD CONSTRAINT fk_booking_service
  FOREIGN KEY (service_id) REFERENCES service;

-- Optional but useful index
CREATE INDEX IF NOT EXISTS idx_booking_service_id ON booking(service_id);
