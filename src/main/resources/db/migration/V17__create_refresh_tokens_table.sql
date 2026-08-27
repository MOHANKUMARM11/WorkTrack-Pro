CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    device_id VARCHAR(100),
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user
    ON refresh_tokens(user_id);

CREATE INDEX idx_refresh_tokens_hash
    ON refresh_tokens(token_hash);

CREATE INDEX idx_refresh_tokens_user_revoked
    ON refresh_tokens(user_id, revoked);
