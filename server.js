const express = require('express');
const session = require('express-session');
const SQLiteStore = require('connect-sqlite3')(session);
const Database = require('better-sqlite3');
const bcrypt = require('bcryptjs');
const crypto = require('crypto');
const path = require('path');

/* ──────────────── Konfiguration ──────────────── */
const PORT = process.env.PORT || 3000;
const DB_PATH = path.join(__dirname, 'zeiterfassung.db');
const SESSION_SECRET = process.env.SESSION_SECRET || crypto.randomBytes(32).toString('hex');
const SESSION_MAX_AGE = 30 * 60 * 1000; // 30 Minuten

const VALID_PROJECTS = [
  'Heizzentrale Nord',
  'Objekt Grünerweg 19',
  'Wohnpark Süd',
  'Service & Wartung',
];
const VALID_TYPES = ['Arbeitszeit', 'Pausenzeit'];

/* ──────────────── Datenbank ──────────────── */
const db = new Database(DB_PATH);
db.pragma('journal_mode = WAL');
db.pragma('foreign_keys = ON');

/* ──────────────── Express App ──────────────── */
const app = express();
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

/* ──────────────── Session ──────────────── */
app.use(
  session({
    store: new SQLiteStore({
      db: 'sessions.db',
      dir: __dirname,
    }),
    secret: SESSION_SECRET,
    resave: false,
    saveUninitialized: false,
    cookie: {
      maxAge: SESSION_MAX_AGE,
      httpOnly: true,
      sameSite: 'lax',
    },
  })
);

/* ──────────────── Middleware: Auth-Check ──────────────── */
function requireAuth(req, res, next) {
  if (!req.session.user) {
    return res.status(401).json({ error: 'Nicht eingeloggt.' });
  }
  // Session bei jeder authentifizierten Anfrage erneuern
  req.session.touch();
  next();
}

/* ──────────────── API: Mitarbeiter-Liste ──────────────── */
app.get('/api/employees', (_req, res) => {
  const employees = db
    .prepare('SELECT id, name FROM employees WHERE active = 1')
    .all();
  res.json(employees);
});

/* ──────────────── API: Login ──────────────── */
app.post('/api/login', (req, res) => {
  const { employeeId, pin } = req.body;

  if (!employeeId || !pin) {
    return res.status(400).json({ error: 'Mitarbeiter-Nr. und PIN erforderlich.' });
  }

  if (!/^\d{4}$/.test(pin)) {
    return res.status(400).json({ error: 'PIN muss 4-stellig sein.' });
  }

  const employee = db
    .prepare('SELECT id, name, pin_hash FROM employees WHERE id = ? AND active = 1')
    .get(employeeId);

  if (!employee) {
    return res.status(401).json({ error: 'Mitarbeiter nicht gefunden.' });
  }

  if (!bcrypt.compareSync(pin, employee.pin_hash)) {
    return res.status(401).json({ error: 'Falscher Login-Code.' });
  }

  req.session.user = { id: employee.id, name: employee.name };
  req.session.activeSegment = null;

  res.json({
    success: true,
    user: { id: employee.id, name: employee.name },
  });
});

/* ──────────────── API: Logout ──────────────── */
app.post('/api/logout', requireAuth, (req, res) => {
  // Laufendes Segment automatisch speichern
  if (req.session.activeSegment) {
    const seg = req.session.activeSegment;
    const endTime = Date.now();
    db.prepare(
      'INSERT INTO time_entries (employee_id, project, type, start_time, end_time) VALUES (?, ?, ?, ?, ?)'
    ).run(req.session.user.id, seg.project, seg.type, seg.start, endTime);
  }

  req.session.destroy((err) => {
    if (err) {
      return res.status(500).json({ error: 'Fehler beim Abmelden.' });
    }
    res.json({ success: true });
  });
});

/* ──────────────── API: Session-Status ──────────────── */
app.get('/api/session', (req, res) => {
  if (!req.session.user) {
    return res.json({ loggedIn: false });
  }
  res.json({
    loggedIn: true,
    user: req.session.user,
    activeSegment: req.session.activeSegment || null,
  });
});

/* ──────────────── API: Segment starten ──────────────── */
app.post('/api/start', requireAuth, (req, res) => {
  const { project, type } = req.body;

  if (!project || !type) {
    return res.status(400).json({ error: 'Projekt und Typ erforderlich.' });
  }

  if (!VALID_PROJECTS.includes(project)) {
    return res.status(400).json({ error: 'Ungültiges Projekt.' });
  }

  if (!VALID_TYPES.includes(type)) {
    return res.status(400).json({ error: 'Ungültiger Typ.' });
  }

  if (req.session.activeSegment) {
    return res.status(409).json({ error: 'Es läuft bereits ein Segment. Bitte zuerst stoppen.' });
  }

  req.session.activeSegment = {
    project,
    type,
    start: Date.now(),
  };

  res.json({
    success: true,
    activeSegment: req.session.activeSegment,
  });
});

/* ──────────────── API: Segment stoppen ──────────────── */
app.post('/api/stop', requireAuth, (req, res) => {
  if (!req.session.activeSegment) {
    return res.status(409).json({ error: 'Kein laufendes Segment.' });
  }

  const seg = req.session.activeSegment;
  const endTime = Date.now();

  const result = db
    .prepare(
      'INSERT INTO time_entries (employee_id, project, type, start_time, end_time) VALUES (?, ?, ?, ?, ?)'
    )
    .run(req.session.user.id, seg.project, seg.type, seg.start, endTime);

  const entry = db.prepare('SELECT * FROM time_entries WHERE id = ?').get(result.lastInsertRowid);

  req.session.activeSegment = null;

  res.json({ success: true, entry });
});

/* ──────────────── API: Einträge abrufen ──────────────── */
app.get('/api/entries', requireAuth, (req, res) => {
  const entries = db
    .prepare(
      `SELECT t.id, t.employee_id, e.name AS employee_name, t.project, t.type,
              t.start_time, t.end_time, t.duration_minutes
       FROM time_entries t
       JOIN employees e ON t.employee_id = e.id
       ORDER BY t.start_time DESC
       LIMIT 500`
    )
    .all();

  res.json(entries);
});

/* ──────────────── API: Eintrag löschen ──────────────── */
app.delete('/api/entries/:id', requireAuth, (req, res) => {
  const entryId = parseInt(req.params.id, 10);

  if (isNaN(entryId)) {
    return res.status(400).json({ error: 'Ungültige Eintrags-ID.' });
  }

  // Nur eigene Einträge löschen
  const entry = db
    .prepare('SELECT * FROM time_entries WHERE id = ?')
    .get(entryId);

  if (!entry) {
    return res.status(404).json({ error: 'Eintrag nicht gefunden.' });
  }

  if (entry.employee_id !== req.session.user.id) {
    return res.status(403).json({ error: 'Keine Berechtigung, diesen Eintrag zu löschen.' });
  }

  db.prepare('DELETE FROM time_entries WHERE id = ?').run(entryId);
  res.json({ success: true });
});

/* ──────────────── API: CSV Export ──────────────── */
app.get('/api/export', requireAuth, (_req, res) => {
  const entries = db
    .prepare(
      `SELECT t.employee_id, e.name AS employee_name, t.project, t.type,
              t.start_time, t.end_time, t.duration_minutes
       FROM time_entries t
       JOIN employees e ON t.employee_id = e.id
       ORDER BY t.start_time DESC`
    )
    .all();

  function csvEscape(val) {
    const str = String(val);
    if (/[;",\n]/.test(str)) {
      return '"' + str.replace(/"/g, '""') + '"';
    }
    return str;
  }

  const header = 'Mitarbeiter-Nr;Name;Projekt;Typ;Start;Ende;Dauer (Minuten)';
  const rows = entries.map((e) =>
    [
      csvEscape(e.employee_id),
      csvEscape(e.employee_name),
      csvEscape(e.project),
      csvEscape(e.type),
      csvEscape(new Date(e.start_time).toISOString()),
      csvEscape(new Date(e.end_time).toISOString()),
      csvEscape(e.duration_minutes),
    ].join(';')
  );

  const bom = '\uFEFF';
  const csv = bom + [header, ...rows].join('\n');

  res.setHeader('Content-Type', 'text/csv; charset=utf-8');
  res.setHeader('Content-Disposition', 'attachment; filename="zeiterfassung.csv"');
  res.send(csv);
});

/* ──────────────── Server starten ──────────────── */
app.listen(PORT, () => {
  console.log(`Zeiterfassung-Server läuft auf http://localhost:${PORT}`);
});
