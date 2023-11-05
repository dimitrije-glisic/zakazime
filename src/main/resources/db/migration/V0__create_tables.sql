CREATE TABLE role
(
  id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(255) UNIQUE NOT NULL
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

CREATE TABLE business_profile
(
  id           INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  status       VARCHAR(50)        NOT NULL,
  name         VARCHAR(50) UNIQUE NOT NULL,
  phone_number VARCHAR(50)        NOT NULL,
  city         VARCHAR(50)        NOT NULL,
  postal_code  VARCHAR(50)        NOT NULL,
  address      VARCHAR(50),
  email        VARCHAR(255),
  description  TEXT,
  created_on   TIMESTAMP          NOT NULL,
  updated_on   TIMESTAMP
);

CREATE TABLE service
(
  id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name        VARCHAR(255) NOT NULL,
  description TEXT         NOT NULL
);

CREATE TABLE service_category
(
  id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name        VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE business_account_map
(
  account_id  INT REFERENCES account (id),
  business_id INT REFERENCES business_profile (id),
  PRIMARY KEY (account_id, business_id)
);

CREATE TABLE service_category_map
(
  service_id  INT REFERENCES service (id),
  category_id INT REFERENCES service_category (id),
  PRIMARY KEY (service_id, category_id)
);

CREATE TABLE business_service_map
(
  business_id INT REFERENCES business_profile (id),
  service_id  INT REFERENCES service (id),
  PRIMARY KEY (business_id, service_id)
);

CREATE TABLE service_provider_map
(
  account_id INT REFERENCES account (id),
  service_id INT REFERENCES service (id),
  PRIMARY KEY (account_id, service_id)
);
