-- Drop FK(s)
ALTER TABLE review DROP CONSTRAINT review_id_fkey;

-- Child to bigint
ALTER TABLE booking_review
  ALTER COLUMN review_id TYPE bigint
  USING review_id::bigint;

-- Parent to bigint
ALTER TABLE review
  ALTER COLUMN id TYPE bigint
  USING id::bigint;

-- Recreate FK
ALTER TABLE booking_review
  ADD CONSTRAINT booking_review_review_id_fkey
  FOREIGN KEY (review_id) REFERENCES review(id);

-- (Optional) keep default sequence wiring if applicable
-- ALTER TABLE review ALTER COLUMN id SET DEFAULT nextval('review_id_seq');
-- ALTER SEQUENCE review_id_seq OWNED BY review.id;
