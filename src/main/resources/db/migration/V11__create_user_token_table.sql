-- V11__create_user_tokens_table.sql
-- Creates user_tokens table used for verification and password-reset tokens

CREATE TABLE IF NOT EXISTS public.user_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    purpose VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

-- Foreign key to users table. Adjust referenced column name if your users PK is different.
ALTER TABLE public.user_tokens
    ADD CONSTRAINT fk_user_tokens_user
    FOREIGN KEY (user_id) REFERENCES public.users (id) ON DELETE CASCADE;

-- Indexes (match the @Index annotations in the entity)
CREATE INDEX IF NOT EXISTS idx_user_token_user ON public.user_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_user_token_purpose ON public.user_tokens (purpose);
CREATE INDEX IF NOT EXISTS idx_user_token_expires ON public.user_tokens (expires_at);

-- Optional: a small housekeeping policy index to speed up purging expired tokens
CREATE INDEX IF NOT EXISTS idx_user_token_used_expires
    ON public.user_tokens (used, expires_at);
