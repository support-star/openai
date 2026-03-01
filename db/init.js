const Database = require('better-sqlite3');
const bcrypt = require('bcryptjs');
const path = require('path');

const DB_PATH = path.join(__dirname, '..', 'zeiterfassung.db');

function initDatabase() {
  const db = new Database(DB_PATH);

  // WAL mode for better concurrent read performance
  db.pragma('journal_mode = WAL');
  db.pragma('foreign_keys = ON');

  // Create tables
  db.exec(`
    CREATE TABLE IF NOT EXISTS employees (
      id TEXT PRIMARY KEY,
      name TEXT NOT NULL,
      pin_hash TEXT NOT NULL,
      active INTEGER DEFAULT 1
    );

    CREATE TABLE IF NOT EXISTS time_entries (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      employee_id TEXT NOT NULL,
      project TEXT NOT NULL,
      type TEXT NOT NULL CHECK(type IN ('Arbeitszeit', 'Pausenzeit')),
      start_time INTEGER NOT NULL,
      end_time INTEGER NOT NULL,
      duration_minutes INTEGER GENERATED ALWAYS AS (ROUND((end_time - start_time) / 60000.0)) STORED,
      created_at TEXT DEFAULT (datetime('now', 'localtime')),
      FOREIGN KEY (employee_id) REFERENCES employees(id)
    );

    CREATE INDEX IF NOT EXISTS idx_entries_employee ON time_entries(employee_id);
    CREATE INDEX IF NOT EXISTS idx_entries_start ON time_entries(start_time DESC);
  `);

  // Seed default employees (only if table is empty)
  const count = db.prepare('SELECT COUNT(*) as c FROM employees').get().c;

  if (count === 0) {
    const SALT_ROUNDS = 10;
    const insert = db.prepare(
      'INSERT INTO employees (id, name, pin_hash) VALUES (?, ?, ?)'
    );

    const defaultEmployees = [
      { id: '1001', name: 'Mitarbeiter 1', pin: '1111' },
      { id: '1002', name: 'Mitarbeiter 2', pin: '2222' },
      { id: '1003', name: 'Mitarbeiter 3', pin: '3333' },
      { id: '1004', name: 'Mitarbeiter 4', pin: '4444' },
      { id: '1005', name: 'Mitarbeiter 5', pin: '5555' },
      { id: '1006', name: 'Mitarbeiter 6', pin: '6666' },
      { id: '1007', name: 'Mitarbeiter 7', pin: '7777' },
      { id: '1008', name: 'Mitarbeiter 8', pin: '8888' },
      { id: '1009', name: 'Mitarbeiter 9', pin: '9999' },
      { id: '1010', name: 'Mitarbeiter 10', pin: '1010' },
    ];

    const insertMany = db.transaction((emps) => {
      for (const emp of emps) {
        const hash = bcrypt.hashSync(emp.pin, SALT_ROUNDS);
        insert.run(emp.id, emp.name, hash);
      }
    });

    insertMany(defaultEmployees);
    console.log(`${defaultEmployees.length} Mitarbeiter angelegt.`);
  } else {
    console.log(`Datenbank existiert bereits (${count} Mitarbeiter).`);
  }

  db.close();
  console.log('Datenbank initialisiert:', DB_PATH);
}

initDatabase();
