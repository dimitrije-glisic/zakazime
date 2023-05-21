--liquibase formatted sql

--changeset liquibase:1
CREATE TABLE accounts
(
    user_id    INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    password   VARCHAR(50)         NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    created_on TIMESTAMP           NOT NULL,
    last_login TIMESTAMP
);

--changeset liquibase:2
CREATE TABLE roles
(
    role_id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    role_name VARCHAR(255) UNIQUE NOT NULL
);

--changeset liquibase:3
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

--changeset liquibase:4
CREATE TABLE service
(
    service_id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    service_name VARCHAR(255) NOT NULL,
    description  TEXT NOT NULL
);

--changeset liquibase:5
CREATE TABLE service_category
(
    category_id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category_name VARCHAR(50) UNIQUE NOT NULL,
    description   TEXT
);

--changeset liquibase:6
CREATE TABLE account_roles_map
(
    user_id    INT REFERENCES accounts (user_id),
    role_id    INT REFERENCES roles (role_id),
    grant_date TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

--changeset liquibase:7
CREATE TABLE business_account_map
(
    user_id     INT REFERENCES accounts (user_id),
    business_id INT REFERENCES business_profile (business_id),
    PRIMARY KEY (user_id, business_id)
);

--changeset liquibase:8
CREATE TABLE service_category_map
(
    service_id  INT REFERENCES service (service_id),
    category_id INT REFERENCES service_category (category_id),
    PRIMARY KEY (service_id, category_id)
);

--changeset liquibase:9
CREATE TABLE business_service_map
(
    business_id INT REFERENCES business_profile (business_id),
    service_id  INT REFERENCES service (service_id),
    PRIMARY KEY (business_id, service_id)
);

--changeset liquibase:10
CREATE TABLE service_provider_map
(
    user_id    INT REFERENCES accounts (user_id),
    service_id INT REFERENCES service (service_id),
    PRIMARY KEY (user_id, service_id)
);
