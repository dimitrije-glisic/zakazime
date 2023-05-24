CREATE TABLE accounts
(
    user_id    INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    password   VARCHAR(50)         NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    created_on TIMESTAMP           NOT NULL,
    last_login TIMESTAMP
);

CREATE TABLE roles
(
    role_id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    role_name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE business_profile
(
    business_id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    business_name VARCHAR(50) UNIQUE NOT NULL,
    description   TEXT               NOT NULL,
    address       VARCHAR(50)        NOT NULL,
    phone_number  VARCHAR(50)        NOT NULL,
    email         VARCHAR(255)       NOT NULL,
    created_on    TIMESTAMP          NOT NULL,
    updated_on    TIMESTAMP
);

CREATE TABLE service
(
    service_id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    service_name VARCHAR(255) NOT NULL,
    description  TEXT NOT NULL
);

CREATE TABLE service_category
(
    category_id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category_name VARCHAR(50) UNIQUE NOT NULL,
    description   TEXT
);

CREATE TABLE account_roles_map
(
    user_id    INT REFERENCES accounts (user_id),
    role_id    INT REFERENCES roles (role_id),
    grant_date TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE business_account_map
(
    user_id     INT REFERENCES accounts (user_id),
    business_id INT REFERENCES business_profile (business_id),
    PRIMARY KEY (user_id, business_id)
);

CREATE TABLE service_category_map
(
    service_id  INT REFERENCES service (service_id),
    category_id INT REFERENCES service_category (category_id),
    PRIMARY KEY (service_id, category_id)
);

CREATE TABLE business_service_map
(
    business_id INT REFERENCES business_profile (business_id),
    service_id  INT REFERENCES service (service_id),
    PRIMARY KEY (business_id, service_id)
);

CREATE TABLE service_provider_map
(
    user_id    INT REFERENCES accounts (user_id),
    service_id INT REFERENCES service (service_id),
    PRIMARY KEY (user_id, service_id)
);
