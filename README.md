# Heizungssteuerungs-Dashboard – Zeiterfassung + n8n Bot

Dieses Repository enthält einen Frontend-Prototypen für **Login- und Zeiterfassung** sowie einen vollständigen **n8n-Bot** mit Telegram-Integration für automatisierte Zeiterfassung und Benachrichtigungen.

## Features

### Frontend (MVP)
- 10 Mitarbeiter (Mitarbeiter-Nummer)
- Login über 4-stelligen PIN-Code
- Zeiterfassung getrennt nach **Arbeitszeit** und **Pausenzeit**
- Zuordnung zu einem Projekt über Dropdown
- Speicherung im Browser (`localStorage`)
- CSV-Export der gespeicherten Einträge

### n8n Bot + Backend
- **Telegram-Bot** – Mitarbeiter können per Chat ein-/ausstempeln
- **Express.js Backend-API** mit PostgreSQL-Datenbank
- **Automatische Tagesberichte** (täglich 18:00 an Admin)
- **Erinnerungen** (morgens 07:00 falls nicht eingestempelt)
- **Warnung bei offenen Sessions** (vergessenes Ausstempeln)

## Architektur

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Telegram   │────▶│     n8n      │────▶│   Backend    │
│   Bot Chat   │◀────│  Workflows   │◀────│  (Express)   │
└──────────────┘     └──────────────┘     └──────┬───────┘
                                                  │
                                           ┌──────▼───────┐
                                           │  PostgreSQL   │
                                           └──────────────┘
```

## Schnellstart

### 1. Umgebungsvariablen einrichten

```bash
cp .env.example .env
# .env bearbeiten und Telegram Bot Token eintragen
```

### 2. Telegram Bot erstellen

1. Öffne [@BotFather](https://t.me/BotFather) in Telegram
2. Sende `/newbot` und folge den Anweisungen
3. Kopiere den Bot-Token in die `.env` Datei

### 3. Alle Services starten

```bash
docker compose up -d
```

Zugang:
- **Frontend**: http://localhost:8000
- **n8n Dashboard**: http://localhost:5678 (Login: admin / changeme)
- **Backend API**: http://localhost:3000

### 4. n8n Workflows importieren

1. Öffne http://localhost:5678
2. Gehe zu **Workflows** → **Import from File**
3. Importiere die JSON-Dateien aus `n8n-workflows/`:
   - `telegram-bot.json` – Telegram-Bot für Zeiterfassung
   - `tagesbericht-cron.json` – Automatischer Tagesbericht
   - `erinnerung-einstempeln.json` – Morgens-Erinnerung
4. Konfiguriere die Telegram-Credentials in n8n mit dem Bot-Token
5. Aktiviere die Workflows

## Telegram Bot – Befehle

| Befehl | Beschreibung |
|--------|-------------|
| `/registrieren <Nr> <PIN>` | Telegram mit Mitarbeiterkonto verknüpfen |
| `/einstempeln [Projekt]` | Arbeit starten (optional mit Projektname) |
| `/ausstempeln` | Arbeit beenden |
| `/pause` | Pause starten oder beenden |
| `/status` | Aktuelle Session anzeigen |
| `/bericht` | Persönlicher Tagesbericht |
| `/hilfe` | Alle Befehle anzeigen |

## API-Endpunkte

| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| POST | `/api/auth/login` | Mitarbeiter-Login |
| POST | `/api/auth/register-telegram` | Telegram-ID verknüpfen |
| POST | `/api/zeit/stempeln` | Ein-/Ausstempeln |
| GET | `/api/zeit/status/:id` | Aktuelle Session |
| GET | `/api/zeit/log/:id?tage=7` | Zeitlog abrufen |
| GET | `/api/zeit/tagesbericht?datum=YYYY-MM-DD` | Tagesbericht |
| GET | `/api/zeit/projekte` | Projekte auflisten |
| POST | `/api/webhooks/telegram` | n8n Telegram-Webhook |
| POST | `/api/webhooks/n8n-notify` | n8n Benachrichtigungen |

## Projektstruktur

```
├── index.html                  # Frontend (MVP)
├── docker-compose.yml          # n8n + PostgreSQL + Backend
├── .env.example                # Umgebungsvariablen-Vorlage
├── backend/
│   ├── Dockerfile
│   ├── package.json
│   ├── server.js               # Express.js Einstiegspunkt
│   ├── db/
│   │   └── init.sql            # Datenbankschema
│   └── routes/
│       ├── auth.js             # Authentifizierung
│       ├── zeit.js             # Zeiterfassung-API
│       └── webhooks.js         # n8n + Telegram Webhooks
└── n8n-workflows/
    ├── telegram-bot.json       # Telegram-Bot Workflow
    ├── tagesbericht-cron.json  # Täglicher Bericht (18:00)
    └── erinnerung-einstempeln.json  # Morgens-Erinnerung (07:00)
```

## Hinweis

Für produktiven Betrieb sollten ergänzt werden:
- Echte bcrypt-gehashte PINs in der Datenbank
- HTTPS/TLS-Terminierung (z.B. über nginx Reverse Proxy)
- Rate Limiting und Input-Validierung
- Backup-Strategie für PostgreSQL
- Rechte-/Mandantenmodell je Gebäude/Objekt
