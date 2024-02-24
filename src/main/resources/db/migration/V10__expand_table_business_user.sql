ALTER TABLE business_user
    ADD COLUMN business_id INTEGER REFERENCES business (id);

ALTER TABLE business_user
    ADD COLUMN email VARCHAR(255);
