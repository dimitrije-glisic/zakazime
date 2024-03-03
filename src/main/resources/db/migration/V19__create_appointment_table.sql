CREATE TABLE appointment
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    client_id   INT references client (id),
    service_id  INT references service (id),
    employee_id INT references employee (id),
--  business_id is redundant, but it's a good idea to have it for performance reasons
    business_id INT references business (id),
    start_time  TIMESTAMP,
    end_time    TIMESTAMP,
    status      VARCHAR(255)
)
