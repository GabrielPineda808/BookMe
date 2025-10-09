
-- 1) add column as nullable
ALTER TABLE public.user_tokens
  ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE NULL;

-- 2) populate existing rows with created_at (or now() if created_at absent)
UPDATE public.user_tokens
SET updated_at = COALESCE(created_at, now())
WHERE updated_at IS NULL;

-- 3) set default for new rows to now()
ALTER TABLE public.user_tokens
  ALTER COLUMN updated_at SET DEFAULT now();

-- 4) make column NOT NULL (only after we've populated values)
ALTER TABLE public.user_tokens
  ALTER COLUMN updated_at SET NOT NULL;
