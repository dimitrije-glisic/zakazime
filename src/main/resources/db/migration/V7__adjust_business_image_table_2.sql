
ALTER TABLE business DROP CONSTRAINT business_profile_image_id_fkey;
ALTER TABLE business DROP COLUMN profile_image_id;

ALTER TABLE business ADD COLUMN profile_image_url VARCHAR(255) NOT NULL DEFAULT '';