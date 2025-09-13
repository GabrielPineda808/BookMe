-- 1) Add the column (nullable first so it won’t fail on existing rows)
ALTER TABLE booking_change_request
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

-- 2) Backfill user_id if you can infer it (optional; depends on your data)
-- UPDATE booking_change_request bcr
-- SET user_id = b.user_id
-- FROM booking b
-- WHERE bcr.booking_id = b.id AND bcr.user_id IS NULL;

-- 3) Add the FK
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_bcr_user'
          AND table_name = 'booking_change_request'
    ) THEN
        ALTER TABLE booking_change_request
            ADD CONSTRAINT fk_bcr_user
            FOREIGN KEY (user_id) REFERENCES users(id);
    END IF;
END $$;

-- 4) If every row must have a requester, enforce NOT NULL after backfill
-- ALTER TABLE booking_change_request
--     ALTER COLUMN user_id SET NOT NULL;

-- 5) Indexes for joins/lookups
CREATE INDEX IF NOT EXISTS idx_bcr_user    ON booking_change_request(user_id);
CREATE INDEX IF NOT EXISTS idx_bcr_booking ON booking_change_request(booking_id);
