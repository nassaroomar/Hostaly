CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    full_name TEXT NOT NULL,
    email TEXT,
    role TEXT NOT NULL CHECK (role IN ('CLIENT', 'GESTORE', 'ADMIN')),
    active INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS rooms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    number TEXT NOT NULL UNIQUE,
    floor INTEGER NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('SINGLE', 'DOUBLE', 'SUITE', 'FAMILY')),
    base_price NUMERIC NOT NULL,
    max_guests INTEGER NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('DISPONIBILE', 'OCCUPATA', 'DA_PULIRE', 'MANUTENZIONE')),
    note TEXT
);

CREATE TABLE IF NOT EXISTS room_features (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id INTEGER NOT NULL REFERENCES rooms(id),
    feature_name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS seasons (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    factor NUMERIC NOT NULL
);

CREATE TABLE IF NOT EXISTS extra_services (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT NOT NULL CHECK (category IN ('BREAKFAST', 'RESTAURANT', 'PARKING', 'LAUNDRY', 'MINIBAR', 'SPA')),
    price NUMERIC NOT NULL,
    description TEXT,
    active INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    guest_id INTEGER NOT NULL REFERENCES users(id),
    guest_name TEXT NOT NULL,
    room_id INTEGER NOT NULL REFERENCES rooms(id),
    check_in TEXT NOT NULL,
    check_out TEXT NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'REJECTED')),
    created_at TEXT NOT NULL,
    confirmed_at TEXT,
    cancelled_at TEXT,
    cancelled_by TEXT,
    cancellation_penalty NUMERIC NOT NULL DEFAULT 0,
    note TEXT
);

CREATE TABLE IF NOT EXISTS booking_service_lines (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id INTEGER NOT NULL REFERENCES bookings(id),
    service_id INTEGER NOT NULL REFERENCES extra_services(id),
    service_name TEXT NOT NULL,
    category TEXT NOT NULL,
    unit_price_snapshot NUMERIC NOT NULL,
    quantity INTEGER NOT NULL,
    service_date TEXT NOT NULL,
    added_by TEXT
);

CREATE TABLE IF NOT EXISTS invoices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id INTEGER NOT NULL REFERENCES bookings(id),
    guest_id INTEGER NOT NULL REFERENCES users(id),
    guest_name TEXT NOT NULL,
    room_number TEXT NOT NULL,
    check_in TEXT NOT NULL,
    check_out TEXT NOT NULL,
    issue_date TEXT NOT NULL,
    discount NUMERIC NOT NULL DEFAULT 0,
    penalty NUMERIC NOT NULL DEFAULT 0,
    total_amount NUMERIC NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('EMESSA', 'PAGATA')),
    paid_at TEXT
);

CREATE TABLE IF NOT EXISTS invoice_lines (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id INTEGER NOT NULL REFERENCES invoices(id),
    kind TEXT NOT NULL,
    description TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC NOT NULL,
    line_date TEXT
);
