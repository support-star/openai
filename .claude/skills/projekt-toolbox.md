# Projekt-Toolbox Skill

Dieses Skill-Set erweitert Claude um projektbezogene Werkzeuge für Qualitätssicherung, Testing und Dokumentation.

## Fähigkeiten

### 1. Code-Qualitätsprüfung (`/quality`)
- HTML-Validierung (Struktur, Semantik, Barrierefreiheit)
- CSS-Analyse (unbenutzte Styles, Konsistenz, Performance)
- JavaScript-Prüfung (Best Practices, Sicherheit, Performance)
- Ergebnis als Bericht mit Prioritäten (Kritisch / Warnung / Hinweis)

### 2. Automatisches Testen (`/test`)
- Erstelle und führe Tests für alle JavaScript-Funktionen aus
- Prüfe UI-Elemente auf korrektes Verhalten
- Teste localStorage-Operationen
- Generiere einen Testbericht mit Bestanden/Fehlgeschlagen-Status

### 3. Dokumentation generieren (`/docs`)
- Erstelle oder aktualisiere die README.md automatisch
- Generiere JSDoc-Kommentare für alle Funktionen
- Erstelle eine Architekturbeschreibung des Projekts
- Generiere eine CHANGELOG.md aus Git-Historie

### 4. Sicherheitsprüfung (`/security`)
- Prüfe auf XSS-Schwachstellen
- Prüfe auf unsichere Datenspeicherung
- Prüfe Input-Validierung
- Prüfe auf hardcodierte Zugangsdaten
- Erstelle einen Sicherheitsbericht

## Anwendung

Wenn der Nutzer einen der obigen Befehle verwendet, führe die entsprechende Analyse durch und präsentiere die Ergebnisse strukturiert auf Deutsch.
