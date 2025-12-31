-- 1. Events missing index
CREATE INDEX idx_events_sales_end ON events (sales_end);

-- 2. Foreign Key Indexes (Postgres doesn't index FKs automatically!)
-- Without these, joins will be slow as your data grows.
CREATE INDEX idx_qr_codes_ticket_id ON qr_codes (ticket_id);
CREATE INDEX idx_tickets_ticket_type_id ON tickets (ticket_type_id);
CREATE INDEX idx_tickets_purchaser_id ON tickets (purchaser_id);
CREATE INDEX idx_ticket_types_event_id ON ticket_types (event_id);
CREATE INDEX idx_ticket_validation_ticket_id ON ticket_validation (ticket_id);
