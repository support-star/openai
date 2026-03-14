# Claude Skills 2.0 – Projektkonfiguration

## Sprache
Kommuniziere immer auf **Deutsch** mit dem Nutzer.

## Projekt
Mitarbeiter-Zeiterfassung (MVP) – Vanilla HTML/CSS/JS, Dark Theme, localStorage.

## Verfügbare Skills

### Projekt-Toolbox
- `/quality` – Code-Qualitätsprüfung (HTML, CSS, JS)
- `/test` – Automatische Tests erstellen und ausführen
- `/docs` – Dokumentation generieren (README, JSDoc, Changelog)
- `/security` – Sicherheitsprüfung (XSS, Zugangsdaten, Input-Validierung)

### Code-Generator
- `/neue-seite <name>` – Neue HTML-Seite im bestehenden Design erstellen
- `/komponente <name>` – Wiederverwendbare UI-Komponente erstellen
- `/feature <beschreibung>` – Neues Feature planen und implementieren
- `/formular <felder>` – Formular mit Validierung generieren
- `/widget <typ>` – Dashboard-Widget erstellen

### DevOps
- `/docker` – Dockerfile und docker-compose einrichten
- `/cicd` – CI/CD Pipeline (GitHub Actions) erstellen
- `/monitoring` – Logging und Statusseite einrichten
- `/backup` – Backup-Strategie implementieren
- `/env` – Umgebungskonfiguration erstellen

## Regeln
- Vanilla HTML/CSS/JS – kein Framework, kein Build-Tool
- Dark Theme beibehalten (Farben aus `:root` in index.html)
- Alle Texte auf Deutsch
- localStorage als Datenspeicher (bis Backend vorhanden)
- Barrierefreiheit beachten
