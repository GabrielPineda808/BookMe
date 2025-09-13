-- V6__add_auditing_columns.sql

-- 1) Columns
ALTER TABLE booking
  ADD COLUMN IF NOT EXISTS created_at timestamp without time zone NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_at timestamp without time zone NOT NULL DEFAULT now();

ALTER TABLE service
  ADD COLUMN IF NOT EXISTS created_at timestamp without time zone NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_at timestamp without time zone NOT NULL DEFAULT now();

ALTER TABLE review
  ADD COLUMN IF NOT EXISTS created_at timestamp without time zone NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_at timestamp without time zone NOT NULL DEFAULT now();

ALTER TABLE users
  ADD COLUMN IF NOT EXISTS created_at timestamp without time zone NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_at timestamp without time zone NOT NULL DEFAULT now();

-- 2) Generic trigger function (runs BEFORE UPDATE)
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN
  NEW.updated_at := now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3) Triggers per table
DO $$
BEGIN
  IF NOT EXISTS (
      SELECT 1 FROM pg_trigger WHERE tgname = 'trg_booking_set_updated_at'
  ) THEN
    CREATE TRIGGER trg_booking_set_updated_at
      BEFORE UPDATE ON booking
      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;

  IF NOT EXISTS (
      SELECT 1 FROM pg_trigger WHERE tgname = 'trg_service_set_updated_at'
  ) THEN
    CREATE TRIGGER trg_service_set_updated_at
      BEFORE UPDATE ON service
      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;

  IF NOT EXISTS (
      SELECT 1 FROM pg_trigger WHERE tgname = 'trg_review_set_updated_at'
  ) THEN
    CREATE TRIGGER trg_review_set_updated_at
      BEFORE UPDATE ON review
      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;

  IF NOT EXISTS (
      SELECT 1 FROM pg_trigger WHERE tgname = 'trg_users_set_updated_at'
  ) THEN
    CREATE TRIGGER trg_users_set_updated_at
      BEFORE UPDATE ON users
      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END$$;
