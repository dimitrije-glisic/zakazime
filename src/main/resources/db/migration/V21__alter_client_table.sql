-- ALTER TABLE working_hours ADD COLUMN is_working_day BOOLEAN DEFAULT TRUE;
ALTER TABLE client
    ADD COLUMN is_guest         BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN guest_email      VARCHAR(255),
    ADD COLUMN guest_phone      VARCHAR(20),
    ADD COLUMN guest_first_name VARCHAR(50),
    ADD COLUMN guest_last_name  VARCHAR(50);

ALTER TABLE client
    RENAME TO customer;

ALTER TABLE appointment
    DROP COLUMN client_id,
    ADD COLUMN customer_id INTEGER REFERENCES customer (id);