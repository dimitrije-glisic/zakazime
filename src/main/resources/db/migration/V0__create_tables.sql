CREATE TABLE role
(
    id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE account
(
    id         INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name VARCHAR(50)         NOT NULL,
    last_name  VARCHAR(50)         NOT NULL,
    password   VARCHAR(50)         NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    is_enabled BOOLEAN             NOT NULL,
    role_id    INT REFERENCES role (id),
    created_on TIMESTAMP           NOT NULL,
    last_login TIMESTAMP
);

CREATE TABLE business_type
(
    id        INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    slug      VARCHAR(50) UNIQUE NOT NULL,
    title     VARCHAR(50) UNIQUE NOT NULL,
    image_url VARCHAR(255)
);

CREATE TABLE predefined_category
(
    id               INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    slug             VARCHAR(50) UNIQUE NOT NULL,
    title            VARCHAR(50) UNIQUE NOT NULL,
    business_type_id INT REFERENCES business_type (id),
    image_url        VARCHAR(255)
);

CREATE TABLE business
(
    id           INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    type_id      INT REFERENCES business_type (id),
    status       VARCHAR(50)        NOT NULL,
    name         VARCHAR(50) UNIQUE NOT NULL,
    phone_number VARCHAR(50)        NOT NULL,
    city         VARCHAR(50)        NOT NULL,
    postal_code  VARCHAR(50)        NOT NULL,
    address      VARCHAR(50),
    description  TEXT,
    created_on   TIMESTAMP          NOT NULL,
    updated_on   TIMESTAMP
);

CREATE TABLE user_defined_category
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title       VARCHAR(50) UNIQUE NOT NULL,
    business_id INT REFERENCES business (id)
);

CREATE TABLE service
(
    id           INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category_id  INT REFERENCES user_defined_category (id),
    name         VARCHAR(50) NOT NULL,
    title        VARCHAR(50) NOT NULL,
    price        DECIMAL     NOT NULL,
    avg_duration INT         NOT NULL,
    description  TEXT
);

-- this table is used to map predefined categories to businesses in order to be able to search for businesses by category
CREATE TABLE BUSINESS_PREDEFINED_CATEGORY_MAP
(
    business_id INT REFERENCES business (id),
    category_id INT REFERENCES predefined_category (id),
    PRIMARY KEY (business_id, category_id)
);

CREATE TABLE business_account_map
(
    account_id  INT REFERENCES account (id),
    business_id INT REFERENCES business (id),
    PRIMARY KEY (account_id, business_id)
);
