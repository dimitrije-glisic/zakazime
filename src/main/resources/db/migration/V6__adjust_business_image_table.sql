ALTER TABLE business_image DROP COLUMN image_type;

ALTER TABLE business ADD COLUMN profile_image_id INTEGER REFERENCES business_image(id);