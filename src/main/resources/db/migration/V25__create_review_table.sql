CREATE TABLE review
(
    id             INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    appointment_id INT UNIQUE NOT NULL REFERENCES appointment (id),
    service        SMALLINT   NOT NULL,
    price_quality  SMALLINT   NOT NULL,
    hygiene        SMALLINT   NOT NULL,
    ambience       SMALLINT   NOT NULL,
    comment        TEXT
);
