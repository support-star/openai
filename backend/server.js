const express = require("express");
const cors = require("cors");
const { Pool } = require("pg");

const app = express();
app.use(cors());
app.use(express.json());

// ── Datenbank-Verbindung ───────────────────────────────────
const pool = new Pool({ connectionString: process.env.DATABASE_URL });

// ── Health-Check ───────────────────────────────────────────
app.get("/health", (_req, res) => res.json({ status: "ok" }));

// ── Routen einbinden ───────────────────────────────────────
const authRoutes = require("./routes/auth");
const zeitRoutes = require("./routes/zeit");
const webhookRoutes = require("./routes/webhooks");

app.use("/api/auth", authRoutes(pool));
app.use("/api/zeit", zeitRoutes(pool));
app.use("/api/webhooks", webhookRoutes(pool));

// ── Server starten ─────────────────────────────────────────
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Backend läuft auf Port ${PORT}`));
