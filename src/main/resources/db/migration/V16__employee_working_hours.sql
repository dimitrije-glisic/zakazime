CREATE TABLE working_hours
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    employee_id INT REFERENCES employee (id),
    day_of_week INT,
    start_time  TIME,
    end_time    TIME
);

CREATE INDEX idx_working_hours_employee_id ON working_hours (employee_id)

