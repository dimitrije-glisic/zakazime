CREATE TABLE client (
    id  INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id INT references account(id),
    business_id INT references business(id)
);
