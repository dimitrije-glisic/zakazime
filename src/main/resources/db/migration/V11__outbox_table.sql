CREATE table outbox
(
    id        INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    recipient VARCHAR(255) NOT NULL,
    subject   VARCHAR(255) NOT NULL,
    body      TEXT         NOT NULL,
    status    VARCHAR(255) NOT NULL
)