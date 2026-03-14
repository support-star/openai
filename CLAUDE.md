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

### Personal (HR)
- `/mitarbeiter` – Mitarbeiterverwaltung (Stammdaten, Qualifikationen)
- `/vertrag <aktion>` – Arbeitsverträge erstellen und verwalten
- `/urlaub` – Urlaubsverwaltung und Krankmeldungen
- `/schichtplan` – Schichtplanung erstellen und verwalten
- `/personalakte <id>` – Personalakte anzeigen und bearbeiten

### Wesen (Organisation)
- `/organisation` – Organigramm und Unternehmensstruktur verwalten
- `/prozess <name>` – Geschäftsprozesse dokumentieren und modellieren
- `/rollen` – Rollen und Berechtigungen definieren
- `/kommunikation` – Mitteilungen, Protokolle und Verteiler
- `/compliance` – Richtlinien und Datenschutz-Dokumentation

### Buchhaltung
- `/konten` – Kontenrahmen (SKR03/SKR04) verwalten
- `/buchen <typ>` – Buchungen erfassen (Einnahmen, Ausgaben, Storno)
- `/ust` – Umsatzsteuer-Voranmeldung und Vorsteuer
- `/auswertung <typ>` – EÜR, GuV, BWA, Offene Posten
- `/zahlung` – Zahlungsverkehr und Mahnwesen

### Angebote
- `/angebot-neu` – Neues Angebot mit Positionen erstellen
- `/angebot-vorlage <typ>` – Branchenspezifische Angebotsvorlagen
- `/kalkulation` – Material-, Lohn- und Gesamtkalkulation
- `/angebot-status` – Angebotsverfolgung und Nachfass
- `/lv` – Leistungsverzeichnis nach GAEB erstellen

### Rechnungen
- `/rechnung-neu` – Rechnung erstellen (§14 UStG konform)
- `/rechnung <typ>` – Abschlags-, Schluss-, Stornorechnung
- `/rechnungen` – Rechnungsübersicht und Status
- `/mahnung <nr>` – Mahnwesen (Erinnerung bis Inkasso)
- `/rechnung-report` – Umsatz- und Zahlungsauswertungen

### Projektmanagement
- `/projekt-neu <name>` – Neues Projekt anlegen
- `/aufgaben` – Aufgaben erstellen, zuweisen, priorisieren
- `/zeitplan <projekt>` – Gantt-Diagramm und Phasenplanung
- `/controlling <projekt>` – Budget, Stunden, Fertigstellungsgrad
- `/bericht <typ>` – Statusberichte und Bautagebuch

### Struktur
- `/struktur-projekt` – Projektstrukturplan (PSP) erstellen
- `/architektur` – Code-Architektur analysieren und optimieren
- `/datenmodell` – Datenstrukturen und Beziehungen entwerfen
- `/navigation` – Seitenstruktur, Sitemap und Menüs
- `/vorlage <typ>` – HTML-Vorlagen (Seite, Formular, Tabelle, Druck)

### Elektronik
- `/schaltplan <typ>` – Schaltpläne als SVG (DIN EN 60617)
- `/bauteile` – Bauteilkatalog und Bestelllisten
- `/steuerung <typ>` – Regelungskonzepte und SPS-Logik
- `/pruefprotokoll <typ>` – VDE-Prüfprotokolle erstellen
- `/edoku` – Elektro-Dokumentation nach DIN 18015

## Regeln
- Vanilla HTML/CSS/JS – kein Framework, kein Build-Tool
- Dark Theme beibehalten (Farben aus `:root` in index.html)
- Alle Texte auf Deutsch
- localStorage als Datenspeicher (bis Backend vorhanden)
- Barrierefreiheit beachten
