-- V5: Add missing versioning for Optimistic Locking

ALTER TABLE events
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE qr_codes
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE users
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;