# Personal (HR) Skill

Dieses Skill-Set erweitert Claude um Personalwesen-Fähigkeiten.

## Fähigkeiten

### 1. Mitarbeiterverwaltung (`/mitarbeiter`)
- Mitarbeiterliste anzeigen, anlegen, bearbeiten, deaktivieren
- Stammdaten verwalten (Name, Adresse, Kontakt, Steuer-ID, SV-Nummer)
- Abteilungs- und Teamzuordnung
- Qualifikationen und Zertifikate erfassen

### 2. Arbeitsverträge (`/vertrag <aktion>`)
- Arbeitsvertrag-Vorlagen generieren (Vollzeit, Teilzeit, Minijob, befristet)
- Vertragsdaten verwalten (Beginn, Ende, Kündigungsfrist, Probezeit)
- Gehalts- und Stundenlohnverwaltung
- Vertragsänderungen dokumentieren

### 3. Urlaubsverwaltung (`/urlaub`)
- Urlaubsanträge erstellen und verwalten
- Urlaubskonto pro Mitarbeiter (Anspruch, genommen, Rest)
- Urlaubskalender / Übersicht
- Krankmeldungen erfassen

### 4. Schichtplanung (`/schichtplan`)
- Schichtpläne erstellen (Früh, Spät, Nacht, Bereitschaft)
- Mitarbeiter Schichten zuweisen
- Konflikte erkennen (Doppelbelegung, Ruhezeiten)
- Wochenübersicht generieren

### 5. Personalakte (`/personalakte <mitarbeiter-id>`)
- Komplette Personalakte anzeigen
- Dokumente und Notizen hinzufügen
- Abmahnungen und Beurteilungen verwalten
- Fortbildungen und Schulungen dokumentieren

## Regeln
- Datenschutz beachten (DSGVO-konform)
- Alle Daten lokal in localStorage (bis Backend vorhanden)
- Deutsche Arbeitsrecht-Begriffe verwenden
- Sensible Daten nie im Klartext loggen
