INSERT INTO role (name)
VALUES ('ADMIN'),
       ('USER'),
       ('SERVICE_PROVIDER');

INSERT INTO account (first_name, last_name, password, email, is_enabled, role_id, created_on)
VALUES ('dimitrije', 'glisic', 'dimi96gm', 'dimitrije@gmail.com', true, 1, '2023-10-31 21:00:00');

-- 'SPORT', 'EDUCATION', 'HEALTH', 'CATERING', 'TRANSPORT', 'TOURISM', 'OTHER'

INSERT INTO public.business_type (title)
VALUES ('ULEPSAVANJE'),
       ('MEDICINA');

-- Insert Categories for Beauty Domain
INSERT INTO service_category (title, business_type_id)
VALUES
    ('Frizerske usluge', 1),
    ('Nega kože', 1),
    ('Nega noktiju', 1),
    ('Šminkanje', 1),
    ('Depilacija', 1);

-- Insert Subcategories for Beauty Domain
-- Assume IDs 1 to 5 for categories
INSERT INTO service_subcategory (title, service_category_id, description)
VALUES
    ('Šišanje', 1, 'Usluge šišanja kose.'),
    ('Farbanje', 1, 'Usluge farbanja kose.'),
    ('Tretmani lica', 2, 'Različiti tretmani za lice.'),
    ('Masaže', 2, 'Različite vrste masaža.'),
    ('Manikir', 3, 'Nega i ulepšavanje noktiju.'),
    ('Pedikir', 3, 'Nega stopala i noktiju.'),
    ('Dnevna šminka', 4, 'Šminkanje za dnevne prilike.'),
    ('Večernja šminka', 4, 'Šminkanje za posebne prilike.'),
    ('Voskom', 5, 'Depilacija voskom.'),
    ('Laserom', 5, 'Depilacija laserom.');

-- Insert Categories for Dental Domain
INSERT INTO service_category (title, business_type_id)
VALUES
    ('Opšta stomatologija', 2),
    ('Ortodoncija', 2),
    ('Pedodontija', 2),
    ('Kozmetička stomatologija', 2),
    ('Parodontologija', 2);

-- Insert Subcategories for Dental Domain
-- Assume IDs 6 to 10 for categories
INSERT INTO service_subcategory (title, service_category_id, description)
VALUES
    ('Pregledi', 6, 'Redovni i specijalistički stomatološki pregledi.'),
    ('Plombiranje', 6, 'Plombiranje i popravke zuba.'),
    ('Stavljanje bravica', 7, 'Postavljanje ortodontskih bravica.'),
    ('Retaineri', 7, 'Nošenje i održavanje retainera.'),
    ('Tretmani za decu', 8, 'Stomatološki tretmani prilagođeni deci.'),
    ('Preventivna zaštita', 8, 'Preventivna zaštita zuba za decu.'),
    ('Izbeljivanje zuba', 9, 'Postupci za izbeljivanje zuba.'),
    ('Veneers', 9, 'Estetski postupci na zubima.'),
    ('Tretman desni', 10, 'Tretmani za zdravlje desni.'),
    ('Duboko čišćenje', 10, 'Duboko čišćenje zuba i desni.');

