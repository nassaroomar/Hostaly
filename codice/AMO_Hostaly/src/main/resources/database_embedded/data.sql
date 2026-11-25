INSERT INTO rooms (number, floor, type, base_price, max_guests, status, note) VALUES
    ('101', 1, 'SINGLE', 80.00, 1, 'DISPONIBILE', NULL),
    ('102', 1, 'DOUBLE', 110.00, 2, 'DISPONIBILE', NULL),
    ('103', 1, 'DOUBLE', 110.00, 2, 'DISPONIBILE', NULL),
    ('104', 1, 'FAMILY', 150.00, 4, 'DISPONIBILE', NULL),
    ('105', 1, 'SINGLE', 80.00, 1, 'DISPONIBILE', NULL),
    ('201', 2, 'DOUBLE', 120.00, 2, 'DISPONIBILE', NULL),
    ('202', 2, 'DOUBLE', 120.00, 2, 'DISPONIBILE', NULL),
    ('203', 2, 'FAMILY', 160.00, 4, 'DISPONIBILE', NULL),
    ('204', 2, 'SUITE', 220.00, 2, 'DISPONIBILE', 'Vista panoramica'),
    ('205', 2, 'SUITE', 240.00, 3, 'DISPONIBILE', 'Vista panoramica');

INSERT INTO seasons (name, start_date, end_date, factor) VALUES
    ('Bassa stagione invernale', '2027-01-07', '2027-03-31', 0.85),
    ('Alta stagione estiva', '2027-06-01', '2027-08-31', 1.30),
    ('Festivita natalizie', '2027-12-20', '2028-01-06', 1.20);

INSERT INTO extra_services (name, category, price, description, active) VALUES
    ('Colazione buffet', 'BREAKFAST', 12.00, 'Colazione a buffet nella sala ristorante', 1),
    ('Colazione in camera', 'BREAKFAST', 18.00, 'Colazione servita direttamente in camera', 1),
    ('Cena al ristorante', 'RESTAURANT', 35.00, 'Menu serale del ristorante dell hotel', 1),
    ('Parcheggio coperto', 'PARKING', 10.00, 'Posto auto coperto per una giornata', 1),
    ('Lavanderia express', 'LAUNDRY', 15.00, 'Lavaggio e stiratura in giornata', 1),
    ('Minibar', 'MINIBAR', 8.00, 'Consumazioni dal minibar della camera', 1),
    ('Accesso SPA', 'SPA', 60.00, 'Ingresso giornaliero alla SPA', 1);
