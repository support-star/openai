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

### Marketing
- `/kampagne <typ>` – Marketing-Kampagnen planen und erstellen
- `/marke` – Corporate Design und Markenauftritt
- `/content <typ>` – Blog-Artikel, Newsletter, Pressemitteilungen
- `/wettbewerb` – Wettbewerbsanalyse und SWOT
- `/marketingplan` – Jahres-Marketingplan mit Budget

### Datenbank
- `/db-design` – Datenbankschema und ER-Diagramme entwerfen
- `/sql <aktion>` – SQL-Queries und Migrationen generieren
- `/storage` – localStorage optimieren und verwalten
- `/backend <typ>` – REST-API und Datenbankanbindung vorbereiten
- `/migration` – Datenmigration (localStorage → DB)

### Betriebsprüfung
- `/pruefung-vorbereiten` – Checkliste und Unterlagen für Betriebsprüfung
- `/dokumente-pruefen` – Rechnungen, Verträge, Kassenbuch prüfen
- `/steuer-check` – USt, Vorsteuer, AfA, Lohnsteuer kontrollieren
- `/audit <bereich>` – Internes Qualitätsaudit durchführen
- `/pruef-bericht` – Prüfbericht mit Maßnahmenplan erstellen

### Organisationen
- `/org-kontakte` – Verbände, Innungen, Kammern verwalten
- `/lieferanten` – Lieferantenstammdaten und Konditionen
- `/behoerden` – Behördenkontakte und Genehmigungen
- `/partner` – Kooperationspartner und Subunternehmer
- `/meldung <typ>` – Pflichtmeldungen an Innungen/Versicherungen

### Zeitmanagement
- `/tagesplan` – Tagesplanung mit Eisenhower-Matrix
- `/wochenplan` – Wochenübersicht und Kapazitätsplanung
- `/termine` – Terminverwaltung und Erinnerungen
- `/zeitauswertung` – Soll/Ist-Vergleich und Produktivität
- `/ressourcen` – Mitarbeiter-Auslastung und Engpässe

### Kundenbindung
- `/kunden` – Kundenstammdaten und CRM
- `/kunden-mail <typ>` – Kundenkommunikation (Willkommen, Wartung, Angebote)
- `/beschwerde` – Beschwerdemanagement und Eskalation
- `/treue` – Treueprogramm und Empfehlungsmarketing
- `/zufriedenheit` – Umfragen (NPS/CSAT) und Bewertungen

### Programmieren
- `/code-analyse` – Code analysieren, Komplexität, Refactoring
- `/api <methode> <endpunkt>` – REST-API-Endpunkte implementieren
- `/frontend <typ>` – Responsive Layouts und UI-Komponenten
- `/chart <typ>` – SVG-Diagramme (Balken, Linien, Kreis, Gauge)
- `/unittest <datei>` – Unit-Tests und Testabdeckung
- `/debug` – Fehleranalyse und Performance-Profiling

### Social Media & Medien
- `/social-strategie` – Social-Media-Strategie für Handwerksbetriebe
- `/social-post <plattform>` – Beiträge für Facebook, Instagram, LinkedIn
- `/community` – Community-Management und Krisenmanagement
- `/presse` – Pressemitteilungen und Medienarbeit
- `/web-profil` – Google My Business, Local SEO, Bewertungen

### Datenschutz
- `/dsgvo` – Verarbeitungsverzeichnis, Datenschutzerklärung, TOMs
- `/einwilligung <typ>` – Einwilligungen und Cookie-Consent
- `/betroffene <recht>` – Auskunft, Löschung, Portabilität (Art. 15-20)
- `/ds-audit` – Datenschutz-Audit und Schulungsunterlagen
- `/datensicherheit` – Passwort-Richtlinien, Verschlüsselung, Notfallplan

### Vermittlung
- `/personal-suche` – Stellenausschreibungen und Bewerbungsmanagement
- `/sub-vergabe` – Subunternehmer finden und beauftragen
- `/auftrag-vermitteln` – Aufträge zuordnen und bestätigen
- `/kooperation` – Gewerke-übergreifende Kooperationen
- `/ausbildung` – Ausbildungsplätze und Berichtsheft-Vorlagen

## Regeln
- Vanilla HTML/CSS/JS – kein Framework, kein Build-Tool
- Dark Theme beibehalten (Farben aus `:root` in index.html)
- Alle Texte auf Deutsch
- localStorage als Datenspeicher (bis Backend vorhanden)
- Barrierefreiheit beachten
