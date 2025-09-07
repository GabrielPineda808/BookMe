-- booking.booking_start -> time
ALTER TABLE booking
  ALTER COLUMN booking_start TYPE time(6)
  USING (
    CASE
      WHEN booking_start IS NULL OR btrim(booking_start) = '' THEN NULL
      -- ISO timestamp-like: 2025-09-07T10:00:00 or 2025-09-07 10:00:00
      WHEN booking_start ~ '^\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}(:\d{2})?$'
        THEN (booking_start::timestamp)::time
      -- 12h with AM/PM, e.g. 10:00 AM or 10:00AM
      WHEN upper(booking_start) ~ '^\s*\d{1,2}:\d{2}(:\d{2})?\s*(AM|PM)\s*$'
        THEN to_timestamp(booking_start, 'HH12:MI:SS AM')::time
      -- 24h HH:MM or HH:MM:SS
      WHEN booking_start ~ '^\d{2}:\d{2}(:\d{2})?$'
        THEN booking_start::time
      ELSE
        NULL  -- or raise exception if you prefer to fail
    END
  );

-- booking.booking_end -> time
ALTER TABLE booking
  ALTER COLUMN booking_end TYPE time(6)
  USING (
    CASE
      WHEN booking_end IS NULL OR btrim(booking_end) = '' THEN NULL
      WHEN booking_end ~ '^\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}(:\d{2})?$'
        THEN (booking_end::timestamp)::time
      WHEN upper(booking_end) ~ '^\s*\d{1,2}:\d{2}(:\d{2})?\s*(AM|PM)\s*$'
        THEN to_timestamp(booking_end, 'HH12:MI:SS AM')::time
      WHEN booking_end ~ '^\d{2}:\d{2}(:\d{2})?$'
        THEN booking_end::time
      ELSE
        NULL
    END
  );

-- service.open -> time
ALTER TABLE service
  ALTER COLUMN open TYPE time(6)
  USING (
    CASE
      WHEN open IS NULL OR btrim(open) = '' THEN NULL
      WHEN open ~ '^\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}(:\d{2})?$'
        THEN (open::timestamp)::time
      WHEN upper(open) ~ '^\s*\d{1,2}:\d{2}(:\d{2})?\s*(AM|PM)\s*$'
        THEN to_timestamp(open, 'HH12:MI:SS AM')::time
      WHEN open ~ '^\d{2}:\d{2}(:\d{2})?$'
        THEN open::time
      ELSE
        NULL
    END
  );

-- service.close -> time
ALTER TABLE service
  ALTER COLUMN close TYPE time(6)
  USING (
    CASE
      WHEN close IS NULL OR btrim(close) = '' THEN NULL
      WHEN close ~ '^\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}(:\d{2})?$'
        THEN (close::timestamp)::time
      WHEN upper(close) ~ '^\s*\d{1,2}:\d{2}(:\d{2})?\s*(AM|PM)\s*$'
        THEN to_timestamp(close, 'HH12:MI:SS AM')::time
      WHEN close ~ '^\d{2}:\d{2}(:\d{2})?$'
        THEN close::time
      ELSE
        NULL
    END
  );
