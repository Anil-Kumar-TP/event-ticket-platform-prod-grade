-- V4: Ensure QR code values are unique to prevent double-entry exploits
ALTER TABLE qr_codes ADD CONSTRAINT uk_qr_code_value UNIQUE (value);

-- Optimization: Add an index for status-based lookups during scans
CREATE INDEX idx_qr_codes_status_value ON qr_codes (status, value);