const { Router } = require("express");

module.exports = function zeitRoutes(pool) {
  const router = Router();

  // POST /api/zeit/stempeln  – Arbeit/Pause starten oder stoppen
  router.post("/stempeln", async (req, res) => {
    const { mitarbeiter_id, projekt_id, typ } = req.body;
    if (!mitarbeiter_id || !typ) {
      return res.status(400).json({ error: "mitarbeiter_id und typ erforderlich" });
    }

    try {
      // Prüfe, ob eine offene Session existiert
      const { rows: offene } = await pool.query(
        "SELECT id, typ FROM zeitlog WHERE mitarbeiter_id = $1 AND end_zeit IS NULL ORDER BY start_zeit DESC LIMIT 1",
        [mitarbeiter_id]
      );

      if (offene.length > 0) {
        // Offene Session schließen
        await pool.query(
          "UPDATE zeitlog SET end_zeit = NOW() WHERE id = $1",
          [offene[0].id]
        );

        const { rows: closed } = await pool.query(
          "SELECT *, dauer_minuten FROM zeitlog WHERE id = $1",
          [offene[0].id]
        );

        return res.json({
          aktion: "gestoppt",
          eintrag: closed[0],
        });
      }

      // Neue Session starten
      const { rows: neu } = await pool.query(
        "INSERT INTO zeitlog (mitarbeiter_id, projekt_id, typ, start_zeit) VALUES ($1, $2, $3, NOW()) RETURNING *",
        [mitarbeiter_id, projekt_id || null, typ]
      );

      res.json({ aktion: "gestartet", eintrag: neu[0] });
    } catch (err) {
      console.error("Stempel-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  // GET /api/zeit/status/:mitarbeiter_id  – Aktuelle Session prüfen
  router.get("/status/:mitarbeiter_id", async (req, res) => {
    try {
      const { rows } = await pool.query(
        `SELECT z.*, p.name AS projekt_name
         FROM zeitlog z
         LEFT JOIN projekte p ON z.projekt_id = p.id
         WHERE z.mitarbeiter_id = $1 AND z.end_zeit IS NULL
         ORDER BY z.start_zeit DESC LIMIT 1`,
        [req.params.mitarbeiter_id]
      );

      if (rows.length === 0) {
        return res.json({ aktiv: false });
      }

      res.json({ aktiv: true, session: rows[0] });
    } catch (err) {
      console.error("Status-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  // GET /api/zeit/log/:mitarbeiter_id  – Zeitlog abrufen
  router.get("/log/:mitarbeiter_id", async (req, res) => {
    const tage = parseInt(req.query.tage) || 7;

    try {
      const { rows } = await pool.query(
        `SELECT z.*, p.name AS projekt_name, m.name AS mitarbeiter_name
         FROM zeitlog z
         LEFT JOIN projekte p ON z.projekt_id = p.id
         LEFT JOIN mitarbeiter m ON z.mitarbeiter_id = m.id
         WHERE z.mitarbeiter_id = $1
           AND z.start_zeit >= NOW() - INTERVAL '1 day' * $2
         ORDER BY z.start_zeit DESC`,
        [req.params.mitarbeiter_id, tage]
      );

      res.json(rows);
    } catch (err) {
      console.error("Log-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  // GET /api/zeit/tagesbericht  – Tagesbericht für alle Mitarbeiter
  router.get("/tagesbericht", async (req, res) => {
    const datum = req.query.datum || new Date().toISOString().slice(0, 10);

    try {
      const { rows } = await pool.query(
        `SELECT m.name, m.personal_nr,
                SUM(CASE WHEN z.typ = 'arbeit' THEN z.dauer_minuten ELSE 0 END) AS arbeit_minuten,
                SUM(CASE WHEN z.typ = 'pause'  THEN z.dauer_minuten ELSE 0 END) AS pause_minuten
         FROM zeitlog z
         JOIN mitarbeiter m ON z.mitarbeiter_id = m.id
         WHERE z.start_zeit::DATE = $1::DATE AND z.end_zeit IS NOT NULL
         GROUP BY m.id, m.name, m.personal_nr
         ORDER BY m.personal_nr`,
        [datum]
      );

      res.json({ datum, bericht: rows });
    } catch (err) {
      console.error("Tagesbericht-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  // GET /api/zeit/projekte  – Alle Projekte auflisten
  router.get("/projekte", async (_req, res) => {
    try {
      const { rows } = await pool.query("SELECT * FROM projekte ORDER BY name");
      res.json(rows);
    } catch (err) {
      console.error("Projekte-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  return router;
};
