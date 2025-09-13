-- Users
ALTER TABLE users
  ALTER COLUMN created_at TYPE timestamp(6) USING created_at::timestamp(6),
  ALTER COLUMN updated_at TYPE timestamp(6) USING updated_at::timestamp(6);

ALTER TABLE users
  ALTER COLUMN created_at SET DEFAULT NOW(),
  ALTER COLUMN updated_at SET DEFAULT NOW();


-- If you added auditing columns to other tables earlier as text/varchar,
-- repeat the same pattern. Uncomment the ones you actually have.

-- Services
ALTER TABLE service
   ALTER COLUMN created_at TYPE timestamp(6) USING created_at::timestamp(6),
   ALTER COLUMN updated_at TYPE timestamp(6) USING updated_at::timestamp(6);

-- Booking
ALTER TABLE booking
    ALTER COLUMN created_at TYPE timestamp(6) USING created_at::timestamp(6),
    ALTER COLUMN updated_at TYPE timestamp(6) USING updated_at::timestamp(6);

-- Review
ALTER TABLE review
    ALTER COLUMN created_at TYPE timestamp(6) USING created_at::timestamp(6),
    ALTER COLUMN updated_at TYPE timestamp(6) USING updated_at::timestamp(6);


