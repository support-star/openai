-- ============================================================
-- Zeiterfassung – Datenbankschema
-- ============================================================

CREATE TABLE IF NOT EXISTS mitarbeiter (
    id            SERIAL PRIMARY KEY,
    personal_nr   VARCHAR(10) UNIQUE NOT NULL,
    name          VARCHAR(100) NOT NULL,
    pin_hash      VARCHAR(255) NOT NULL,
    telegram_id   BIGINT UNIQUE,
    aktiv         BOOLEAN DEFAULT TRUE,
    erstellt_am   TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS projekte (
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(200) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS zeitlog (
    id              SERIAL PRIMARY KEY,
    mitarbeiter_id  INT REFERENCES mitarbeiter(id),
    projekt_id      INT REFERENCES projekte(id),
    typ             VARCHAR(20) NOT NULL CHECK (typ IN ('arbeit', 'pause')),
    start_zeit      TIMESTAMPTZ NOT NULL,
    end_zeit        TIMESTAMPTZ,
    dauer_minuten   INT GENERATED ALWAYS AS (
        CASE WHEN end_zeit IS NOT NULL
             THEN EXTRACT(EPOCH FROM (end_zeit - start_zeit))::INT / 60
             ELSE NULL
        END
    ) STORED,
    erstellt_am     TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_zeitlog_mitarbeiter ON zeitlog(mitarbeiter_id);
CREATE INDEX idx_zeitlog_start       ON zeitlog(start_zeit);

-- ── Standarddaten einfügen ─────────────────────────────────

INSERT INTO projekte (name) VALUES
    ('Heizzentrale Nord'),
    ('Objekt Grünerweg 19'),
    ('Wohnpark Süd'),
    ('Service & Wartung')
ON CONFLICT DO NOTHING;

-- Mitarbeiter mit bcrypt-Hash für die Standard-PINs
-- (In Produktion: echte gehashte PINs verwenden)
INSERT INTO mitarbeiter (personal_nr, name, pin_hash) VALUES
    ('1001', 'Mitarbeiter 1',  '$2b$10$placeholder_hash_1001'),
    ('1002', 'Mitarbeiter 2',  '$2b$10$placeholder_hash_1002'),
    ('1003', 'Mitarbeiter 3',  '$2b$10$placeholder_hash_1003'),
    ('1004', 'Mitarbeiter 4',  '$2b$10$placeholder_hash_1004'),
    ('1005', 'Mitarbeiter 5',  '$2b$10$placeholder_hash_1005'),
    ('1006', 'Mitarbeiter 6',  '$2b$10$placeholder_hash_1006'),
    ('1007', 'Mitarbeiter 7',  '$2b$10$placeholder_hash_1007'),
    ('1008', 'Mitarbeiter 8',  '$2b$10$placeholder_hash_1008'),
    ('1009', 'Mitarbeiter 9',  '$2b$10$placeholder_hash_1009'),
    ('1010', 'Mitarbeiter 10', '$2b$10$placeholder_hash_1010')
ON CONFLICT DO NOTHING;
