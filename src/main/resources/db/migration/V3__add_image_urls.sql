ALTER TABLE business_type
    ADD COLUMN image_url VARCHAR(255);

ALTER TABLE service_category
    ADD COLUMN image_url VARCHAR(255);

ALTER TABLE service_subcategory
    ADD COLUMN image_url VARCHAR(255);

ALTER TABLE service
    ADD COLUMN image_url VARCHAR(255);