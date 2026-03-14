# Rechnungen Skill

Dieses Skill-Set erweitert Claude um Fähigkeiten zur Rechnungserstellung und -verwaltung.

## Fähigkeiten

### 1. Rechnung erstellen (`/rechnung-neu`)
- Rechnung aus Angebot oder frei erstellen
- Pflichtangaben nach §14 UStG automatisch einfügen
- Rechnungsnummer fortlaufend vergeben
- Positionen mit MwSt-Sätzen berechnen
- Skonto und Zahlungsziel angeben

### 2. Rechnungstypen (`/rechnung <typ>`)
- Abschlagsrechnung / Teilrechnung
- Schlussrechnung
- Gutschrift / Stornorechnung
- Proforma-Rechnung
- Kleinunternehmer-Rechnung (§19 UStG)

### 3. Rechnungsverwaltung (`/rechnungen`)
- Alle Rechnungen auflisten und filtern
- Status verwalten (Erstellt, Versendet, Bezahlt, Überfällig, Storniert)
- Offene Posten anzeigen
- Zahlungseingänge zuordnen
- Fälligkeiten überwachen

### 4. Mahnwesen (`/mahnung <rechnung-nr>`)
- Zahlungserinnerung erstellen
- 1./2./3. Mahnung mit Fristsetzung
- Mahngebühren und Verzugszinsen berechnen
- Mahnhistorie dokumentieren
- Inkasso-Übergabe vorbereiten

### 5. Auswertungen (`/rechnung-report`)
- Umsatzübersicht nach Zeitraum, Kunde, Projekt
- Offene-Posten-Liste mit Altersstruktur
- Zahlungsmoral-Analyse pro Kunde
- Umsatzsteuer-Zusammenfassung
- Export als CSV oder druckfähiges HTML

## Regeln
- §14 UStG Pflichtangaben immer einhalten
- Fortlaufende, lückenlose Rechnungsnummern
- GoBD-konforme Aufbewahrung
- Alle Beträge in EUR, netto + brutto ausweisen
- Zahlungsziel standardmäßig 14 Tage
