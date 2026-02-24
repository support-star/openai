# Heizungssteuerungs-Dashboard – Zeiterfassung (MVP)

Dieses Repository enthält einen einfachen Frontend-Prototypen für die gewünschte **Login- und Zeiterfassung**:

- 10 Mitarbeiter (Mitarbeiter-Nummer)
- Login über 4-stelligen PIN-Code
- Zeiterfassung getrennt nach **Arbeitszeit** und **Pausenzeit**
- Zuordnung zu einem Projekt über Dropdown
- Speicherung im Browser (`localStorage`)
- CSV-Export der gespeicherten Einträge

## Start

```bash
python3 -m http.server 8000
```

Dann öffnen:

- <http://localhost:8000>

## Hinweis

Der aktuelle Stand ist ein MVP ohne Server/Backend. Für produktiven Betrieb sollten ergänzt werden:

- Backend-API (z. B. Node-RED/Express/FastAPI)
- Datenbank (z. B. InfluxDB + relationale User-Tabelle)
- Sichere Authentifizierung (PIN gehasht, Rollen, Session-Handling)
- Rechte-/Mandantenmodell je Gebäude/Objekt
