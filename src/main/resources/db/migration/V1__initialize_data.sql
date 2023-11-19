INSERT INTO role (name)
VALUES ('ADMIN'),
       ('USER'),
       ('SERVICE_PROVIDER');

INSERT INTO account (first_name, last_name, password, email, is_enabled, role_id, created_on)
VALUES ('dimitrije', 'glisic', 'dimi96gm', 'dimitrije@gmail.com', true, 1, '2023-10-31 21:00:00');


-- 'SPORT', 'EDUCATION', 'HEALTH', 'CATERING', 'TRANSPORT', 'TOURISM', 'OTHER'

INSERT INTO public.business_type (name)
VALUES ('BEAUTY'),
       ('HEALTH');

INSERT INTO service_category (name, business_type_id)
VALUES ('HAIR', 1),
       ('NAILS', 1),
       ('FACIAL', 1),
       ('DENTAL', 2);

