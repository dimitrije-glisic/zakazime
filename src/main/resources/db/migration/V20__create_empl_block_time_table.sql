CREATE TABLE employee_block_time
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    employee_id INT references employee (id),
    start_time  TIMESTAMP,
    end_time    TIMESTAMP
);
