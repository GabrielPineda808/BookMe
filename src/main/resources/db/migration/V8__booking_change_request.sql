-- V8__booking_change_request.sql
CREATE TYPE change_request_actor AS ENUM ('USER', 'OWNER');
CREATE TYPE change_request_status AS ENUM ('PENDING','APPROVED','DECLINED','CANCELLED','EXPIRED');

CREATE TABLE booking_change_request (
  id BIGSERIAL PRIMARY KEY,
  booking_id BIGINT NOT NULL REFERENCES booking(id) ON DELETE CASCADE,
  requested_by change_request_actor NOT NULL,
  current_booking_date date NOT NULL,
  current_start time NOT NULL,
  current_end time NOT NULL,
  proposed_date date NOT NULL,
  proposed_start time NOT NULL,
  proposed_end time NOT NULL,
  status change_request_status NOT NULL DEFAULT 'PENDING',
  reason text,
  response_reason text,
  expires_at timestamp(6),
  created_at timestamp(6) NOT NULL DEFAULT NOW(),
  updated_at timestamp(6) NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bcr_booking ON booking_change_request(booking_id);
CREATE INDEX idx_bcr_status ON booking_change_request(status);
