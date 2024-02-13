CREATE TABLE business_image
(
    id            INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    business_id   INT REFERENCES business (id),
    image_url     TEXT NOT NULL,
    image_type    VARCHAR(50) CHECK (image_type IN ('profile', 'gallery')),
    created_on    TIMESTAMP NOT NULL,
    updated_on    TIMESTAMP
);
