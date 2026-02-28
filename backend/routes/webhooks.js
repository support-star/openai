const { Router } = require("express");

module.exports = function webhookRoutes(pool) {
  const router = Router();

  // POST /api/webhooks/telegram  – Eingehende Telegram-Nachrichten (von n8n weitergeleitet)
  router.post("/telegram", async (req, res) => {
    const { telegram_id, command, text } = req.body;

    if (!telegram_id) {
      return res.status(400).json({ error: "telegram_id erforderlich" });
    }

    try {
      // Mitarbeiter anhand Telegram-ID finden
      const { rows: mitarbeiter } = await pool.query(
        "SELECT id, name, personal_nr FROM mitarbeiter WHERE telegram_id = $1 AND aktiv = TRUE",
        [telegram_id]
      );

      if (mitarbeiter.length === 0) {
        return res.json({
          antwort:
            "Du bist noch nicht registriert. Sende /registrieren <Personalnummer> <PIN> um dich zu verknüpfen.",
        });
      }

      const ma = mitarbeiter[0];

      switch (command) {
        case "/start":
        case "/hilfe":
          return res.json({
            antwort: `Hallo ${ma.name}! Verfügbare Befehle:\n` +
              `/einstempeln [Projekt] – Arbeit starten\n` +
              `/ausstempeln – Arbeit beenden\n` +
              `/pause – Pause starten/beenden\n` +
              `/status – Aktuelle Session anzeigen\n` +
              `/bericht – Tagesbericht abrufen`,
          });

        case "/einstempeln": {
          // Prüfe ob bereits eingestempelt
          const { rows: offen } = await pool.query(
            "SELECT id FROM zeitlog WHERE mitarbeiter_id = $1 AND end_zeit IS NULL AND typ = 'arbeit'",
            [ma.id]
          );

          if (offen.length > 0) {
            return res.json({ antwort: "Du bist bereits eingestempelt!" });
          }

          // Projekt finden (optional)
          let projektId = null;
          if (text) {
            const { rows: projekte } = await pool.query(
              "SELECT id, name FROM projekte WHERE LOWER(name) LIKE LOWER($1)",
              [`%${text}%`]
            );
            if (projekte.length > 0) projektId = projekte[0].id;
          }

          await pool.query(
            "INSERT INTO zeitlog (mitarbeiter_id, projekt_id, typ, start_zeit) VALUES ($1, $2, 'arbeit', NOW())",
            [ma.id, projektId]
          );

          const projektText = projektId ? ` (${text})` : "";
          return res.json({
            antwort: `Eingestempelt um ${new Date().toLocaleTimeString("de-DE")}${projektText}. Guten Start!`,
          });
        }

        case "/ausstempeln": {
          const { rows: offen } = await pool.query(
            "SELECT id FROM zeitlog WHERE mitarbeiter_id = $1 AND end_zeit IS NULL AND typ = 'arbeit'",
            [ma.id]
          );

          if (offen.length === 0) {
            return res.json({ antwort: "Du bist nicht eingestempelt." });
          }

          await pool.query(
            "UPDATE zeitlog SET end_zeit = NOW() WHERE id = $1",
            [offen[0].id]
          );

          const { rows: closed } = await pool.query(
            "SELECT dauer_minuten FROM zeitlog WHERE id = $1",
            [offen[0].id]
          );

          return res.json({
            antwort: `Ausgestempelt. Arbeitszeit: ${closed[0].dauer_minuten} Minuten. Feierabend!`,
          });
        }

        case "/pause": {
          const { rows: offenePause } = await pool.query(
            "SELECT id FROM zeitlog WHERE mitarbeiter_id = $1 AND end_zeit IS NULL AND typ = 'pause'",
            [ma.id]
          );

          if (offenePause.length > 0) {
            await pool.query(
              "UPDATE zeitlog SET end_zeit = NOW() WHERE id = $1",
              [offenePause[0].id]
            );
            return res.json({ antwort: "Pause beendet. Weiter geht's!" });
          }

          await pool.query(
            "INSERT INTO zeitlog (mitarbeiter_id, typ, start_zeit) VALUES ($1, 'pause', NOW())",
            [ma.id]
          );
          return res.json({ antwort: "Pause gestartet. Gute Erholung!" });
        }

        case "/status": {
          const { rows } = await pool.query(
            `SELECT z.typ, z.start_zeit, p.name AS projekt_name
             FROM zeitlog z
             LEFT JOIN projekte p ON z.projekt_id = p.id
             WHERE z.mitarbeiter_id = $1 AND z.end_zeit IS NULL
             ORDER BY z.start_zeit DESC`,
            [ma.id]
          );

          if (rows.length === 0) {
            return res.json({ antwort: "Keine aktive Session." });
          }

          const s = rows[0];
          const seit = new Date(s.start_zeit).toLocaleTimeString("de-DE");
          const projekt = s.projekt_name ? ` | Projekt: ${s.projekt_name}` : "";
          return res.json({
            antwort: `Aktiv: ${s.typ === "arbeit" ? "Arbeit" : "Pause"} seit ${seit}${projekt}`,
          });
        }

        case "/bericht": {
          const { rows } = await pool.query(
            `SELECT
                SUM(CASE WHEN typ = 'arbeit' THEN dauer_minuten ELSE 0 END) AS arbeit,
                SUM(CASE WHEN typ = 'pause'  THEN dauer_minuten ELSE 0 END) AS pause
             FROM zeitlog
             WHERE mitarbeiter_id = $1
               AND start_zeit::DATE = CURRENT_DATE
               AND end_zeit IS NOT NULL`,
            [ma.id]
          );

          const r = rows[0];
          const arbeit = r.arbeit || 0;
          const pause = r.pause || 0;
          return res.json({
            antwort:
              `Tagesbericht für ${ma.name}:\n` +
              `Arbeitszeit: ${Math.floor(arbeit / 60)}h ${arbeit % 60}min\n` +
              `Pausenzeit: ${Math.floor(pause / 60)}h ${pause % 60}min`,
          });
        }

        default:
          return res.json({
            antwort: "Unbekannter Befehl. Sende /hilfe für eine Übersicht.",
          });
      }
    } catch (err) {
      console.error("Webhook-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  // POST /api/webhooks/n8n-notify  – n8n kann Benachrichtigungen auslösen
  router.post("/n8n-notify", async (req, res) => {
    const { type, data } = req.body;

    try {
      switch (type) {
        case "tagesbericht": {
          const { rows } = await pool.query(
            `SELECT m.name, m.personal_nr, m.telegram_id,
                    SUM(CASE WHEN z.typ = 'arbeit' THEN z.dauer_minuten ELSE 0 END) AS arbeit,
                    SUM(CASE WHEN z.typ = 'pause'  THEN z.dauer_minuten ELSE 0 END) AS pause
             FROM zeitlog z
             JOIN mitarbeiter m ON z.mitarbeiter_id = m.id
             WHERE z.start_zeit::DATE = CURRENT_DATE AND z.end_zeit IS NOT NULL
             GROUP BY m.id, m.name, m.personal_nr, m.telegram_id`
          );

          return res.json({
            bericht: rows,
            nachricht: rows
              .map(
                (r) =>
                  `${r.name}: ${Math.floor(r.arbeit / 60)}h ${r.arbeit % 60}min Arbeit, ${r.pause || 0}min Pause`
              )
              .join("\n"),
          });
        }

        case "offene_sessions": {
          const { rows } = await pool.query(
            `SELECT m.name, m.telegram_id, z.typ, z.start_zeit
             FROM zeitlog z
             JOIN mitarbeiter m ON z.mitarbeiter_id = m.id
             WHERE z.end_zeit IS NULL`
          );

          return res.json({
            offene: rows,
            nachricht:
              rows.length > 0
                ? `${rows.length} offene Session(s):\n` +
                  rows.map((r) => `${r.name}: ${r.typ} seit ${new Date(r.start_zeit).toLocaleTimeString("de-DE")}`).join("\n")
                : "Keine offenen Sessions.",
          });
        }

        default:
          return res.json({ error: "Unbekannter Benachrichtigungstyp" });
      }
    } catch (err) {
      console.error("n8n-Notify-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  return router;
};
