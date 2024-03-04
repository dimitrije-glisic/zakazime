ALTER TABLE customer
    DROP COLUMN guest_email,
    ADD COLUMN email      VARCHAR(255),
    DROP COLUMN guest_phone,
    ADD COLUMN phone      VARCHAR(20),
    DROP COLUMN guest_first_name,
    ADD COLUMN first_name VARCHAR(50),
    DROP COLUMN guest_last_name,
    ADD COLUMN last_name  VARCHAR(50);