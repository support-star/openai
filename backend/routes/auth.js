const { Router } = require("express");
const bcrypt = require("bcrypt");

module.exports = function authRoutes(pool) {
  const router = Router();

  // POST /api/auth/login  – Mitarbeiter-Login per PIN
  router.post("/login", async (req, res) => {
    const { personal_nr, pin } = req.body;
    if (!personal_nr || !pin) {
      return res.status(400).json({ error: "personal_nr und pin erforderlich" });
    }

    try {
      const { rows } = await pool.query(
        "SELECT id, name, pin_hash FROM mitarbeiter WHERE personal_nr = $1 AND aktiv = TRUE",
        [personal_nr]
      );

      if (rows.length === 0) {
        return res.status(401).json({ error: "Ungültige Anmeldedaten" });
      }

      const mitarbeiter = rows[0];
      const pinValid = await bcrypt.compare(pin, mitarbeiter.pin_hash);
      if (!pinValid) {
        return res.status(401).json({ error: "Ungültige Anmeldedaten" });
      }

      res.json({
        id: mitarbeiter.id,
        name: mitarbeiter.name,
        personal_nr,
      });
    } catch (err) {
      console.error("Login-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  // POST /api/auth/register-telegram  – Telegram-ID mit Mitarbeiter verknüpfen
  router.post("/register-telegram", async (req, res) => {
    const { personal_nr, pin, telegram_id } = req.body;
    if (!personal_nr || !pin || !telegram_id) {
      return res
        .status(400)
        .json({ error: "personal_nr, pin und telegram_id erforderlich" });
    }

    try {
      const { rows } = await pool.query(
        "SELECT id, pin_hash FROM mitarbeiter WHERE personal_nr = $1 AND aktiv = TRUE",
        [personal_nr]
      );

      if (rows.length === 0) {
        return res.status(401).json({ error: "Ungültige Anmeldedaten" });
      }

      const pinValid = await bcrypt.compare(pin, rows[0].pin_hash);
      if (!pinValid) {
        return res.status(401).json({ error: "Ungültige Anmeldedaten" });
      }

      await pool.query(
        "UPDATE mitarbeiter SET telegram_id = $1 WHERE id = $2",
        [telegram_id, rows[0].id]
      );

      res.json({ success: true, message: "Telegram verknüpft" });
    } catch (err) {
      console.error("Telegram-Registrierung-Fehler:", err);
      res.status(500).json({ error: "Serverfehler" });
    }
  });

  return router;
};
