ALTER TABLE business
    ADD COLUMN contact_person VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE business
    ADD COLUMN email VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE business
    ADD COLUMN service_kind VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE business
    ADD COLUMN year_of_establishment INT NOT NULL DEFAULT 0;
