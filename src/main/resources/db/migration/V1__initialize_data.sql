INSERT INTO role (name)
VALUES ('ADMIN'),
       ('USER'),
       ('SERVICE_PROVIDER');

INSERT INTO account (first_name, last_name, password, email, is_enabled, role_id, created_on)
VALUES ('dimitrije', 'glisic', 'dimi96gm', 'dimitrije@gmail.com', true, 1, '2023-10-31 21:00:00');
