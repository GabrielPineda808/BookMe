ALTER TABLE users
  ALTER COLUMN location_address DROP NOT NULL,
  ALTER COLUMN location_city DROP NOT NULL,
  ALTER COLUMN location_state DROP NOT NULL,
  ALTER COLUMN location_country DROP NOT NULL,
  ALTER COLUMN location_area_code DROP NOT NULL,
  ALTER COLUMN phone DROP NOT NULL;
