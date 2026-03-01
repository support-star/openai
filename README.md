# Kurtech Zeiterfassung

Mitarbeiter-Zeiterfassung für Heizungsprojekte mit Backend, Datenbank und sicherer Authentifizierung.

## Features

- Login mit Mitarbeiter-Nr. und 4-stelligem PIN (serverseitig gehasht mit bcrypt)
- Arbeitszeit und Pausenzeit getrennt erfassen
- Zuordnung zu Projekten (Heizzentrale Nord, Objekt Grünerweg 19, Wohnpark Süd, Service & Wartung)
- Live-Timer während aktiver Zeiterfassung
- Einträge löschen (nur eigene)
- CSV-Export (Excel-kompatibel mit BOM)
- Automatische Session-Verwaltung (30 Min. Timeout)
- Responsives Design für Desktop und Mobil

## Tech-Stack

- **Backend**: Node.js + Express
- **Datenbank**: SQLite (via better-sqlite3)
- **Sessions**: express-session mit SQLite-Store
- **Authentifizierung**: bcryptjs (PIN-Hashing)
- **Frontend**: Vanilla HTML/CSS/JavaScript

## Installation

```bash
# Dependencies installieren
npm install

# Datenbank initialisieren (erstellt Tabellen + 10 Demo-Mitarbeiter)
npm run init-db

# Server starten
npm start
```

Dann öffnen: http://localhost:3000

## Demo-Zugangsdaten

| Mitarbeiter-Nr. | PIN  |
|-----------------|------|
| 1001            | 1111 |
| 1002            | 2222 |
| 1003            | 3333 |
| 1004            | 4444 |
| 1005            | 5555 |
| 1006            | 6666 |
| 1007            | 7777 |
| 1008            | 8888 |
| 1009            | 9999 |
| 1010            | 1010 |

## API-Endpunkte

| Methode | Pfad              | Beschreibung                |
|---------|-------------------|-----------------------------|
| GET     | /api/employees    | Mitarbeiterliste             |
| POST    | /api/login        | Login (employeeId, pin)      |
| POST    | /api/logout       | Logout                       |
| GET     | /api/session      | Session-Status prüfen        |
| POST    | /api/start        | Zeitsegment starten          |
| POST    | /api/stop         | Zeitsegment stoppen          |
| GET     | /api/entries      | Alle Einträge abrufen        |
| DELETE  | /api/entries/:id  | Eintrag löschen (nur eigene) |
| GET     | /api/export       | CSV-Export herunterladen      |

## Umgebungsvariablen

| Variable       | Standard           | Beschreibung           |
|----------------|--------------------|------------------------|
| PORT           | 3000               | Server-Port            |
| SESSION_SECRET | (zufällig generiert) | Session-Verschlüsselung |

## Projektstruktur

```
├── server.js          # Express-Backend mit API-Routen
├── package.json       # Abhängigkeiten
├── db/
│   └── init.js        # Datenbank-Initialisierung
├── public/
│   └── index.html     # Frontend (Single Page App)
└── README.md
```
