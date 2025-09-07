ALTER TABLE booking
    ADD COLUMN IF NOT EXISTS booking_date date;

CREATE INDEX IF NOT EXISTS idx_booking_service_date
  ON booking(service_id, booking_date);
