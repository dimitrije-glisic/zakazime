CREATE table employee
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id  INT REFERENCES account (id),
    business_id INT REFERENCES business (id),
    name        VARCHAR(100),
    phone       VARCHAR(100),
    email       VARCHAR(100),
    active      BOOLEAN
);

CREATE INDEX employee_account_id_index ON employee (account_id);
CREATE INDEX employee_business_id_index ON employee (business_id);

CREATE TABLE employee_account_map
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    employee_id INT REFERENCES employee (id),
    account_id  INT REFERENCES account (id)
);

CREATE INDEX employee_account_map_employee_id_index ON employee_account_map (employee_id);
CREATE INDEX employee_account_map_account_id_index ON employee_account_map (account_id);


