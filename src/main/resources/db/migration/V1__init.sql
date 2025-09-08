-- === USERS ==============================================================
CREATE TABLE users (
    id                          BIGSERIAL PRIMARY KEY,
    email                       VARCHAR(255) NOT NULL UNIQUE,
    password                    VARCHAR(255) NOT NULL,
    role                        VARCHAR(50)  NOT NULL,              -- Enum stored as STRING (Role)
    first_name                  VARCHAR(100) NOT NULL,
    last_name                   VARCHAR(100) NOT NULL,
    phone                       VARCHAR(50),

    created_at                  VARCHAR(50)  NOT NULL,              -- matches String in entity
    enabled                     BOOLEAN      NOT NULL,

    verification_code           VARCHAR(255),
    verification_expiration     TIMESTAMP,

    -- Embedded Location (NOT NULL because fields in @Embeddable are nullable=false)
    location_address            VARCHAR(255) NOT NULL,
    location_city               VARCHAR(120) NOT NULL,
    location_state              VARCHAR(64)  NOT NULL,
    location_area_code          VARCHAR(32)  NOT NULL,
    location_country            VARCHAR(64)  NOT NULL
);

CREATE INDEX idx_users_email ON users(email);

-- === SERVICE ============================================================
CREATE TABLE service (
    service_id                  SERIAL PRIMARY KEY,
    user_id                     BIGINT      NOT NULL,
    handle                      VARCHAR(255) NOT NULL,
    service_name                VARCHAR(255) NOT NULL,

    -- Embedded Location (same columns/names used in @AttributeOverrides)
    location_address            VARCHAR(255) NOT NULL,
    location_city               VARCHAR(120) NOT NULL,
    location_state              VARCHAR(64)  NOT NULL,
    location_area_code          VARCHAR(32)  NOT NULL,
    location_country            VARCHAR(64)  NOT NULL,

    description                 TEXT,
    interval                    INTEGER     NOT NULL,
    open                        VARCHAR(50) NOT NULL,               -- stored as String in entity
    close                       VARCHAR(50) NOT NULL,               -- stored as String in entity

    CONSTRAINT fk_service_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_service_user    ON service(user_id);
CREATE INDEX idx_service_handle  ON service(handle);

-- === REVIEW =============================================================
CREATE TABLE review (
    id                          SERIAL PRIMARY KEY,
    user_id                     BIGINT  NOT NULL,
    service_id                  BIGINT NOT NULL,
    rating                      INTEGER,
    comment                     TEXT,

    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_review_service
        FOREIGN KEY (service_id) REFERENCES service(service_id)
);

CREATE INDEX idx_review_user    ON review(user_id);
CREATE INDEX idx_review_service ON review(service_id);

-- === BOOKING ============================================================
CREATE TABLE booking (
    id                          SERIAL PRIMARY KEY,
    user_id                     BIGINT  NOT NULL,
    service_id                  BIGINT NOT NULL,

    status                      VARCHAR(50) NOT NULL,               -- Enum stored as STRING (BookingStatus)
    booking_start               VARCHAR(255) NOT NULL,
    booking_end                 VARCHAR(255) NOT NULL,
    notes                       TEXT,

    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_booking_service
        FOREIGN KEY (service_id) REFERENCES service(service_id)
);

CREATE INDEX idx_booking_user    ON booking(user_id);
CREATE INDEX idx_booking_service ON booking(service_id);
CREATE INDEX idx_booking_status  ON booking(status);
