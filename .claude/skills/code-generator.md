# Code-Generator Skill

Dieses Skill-Set erweitert Claude um die Fähigkeit, neue Seiten, Komponenten und Features automatisch zu generieren.

## Fähigkeiten

### 1. Neue Seite erstellen (`/neue-seite <name>`)
- Erstelle eine neue HTML-Seite im bestehenden Dark-Theme-Design
- Übernehme automatisch das CSS aus `index.html`
- Füge Navigation zwischen den Seiten hinzu
- Passe die Seite an den angegebenen Zweck an

### 2. Komponente erstellen (`/komponente <name>`)
- Erstelle wiederverwendbare UI-Komponenten (Cards, Formulare, Tabellen, Modals)
- Verwende das bestehende Design-System (Farben, Schriftarten, Abstände)
- Generiere zugehöriges JavaScript mit Event-Handling
- Integriere die Komponente in bestehende Seiten

### 3. Feature hinzufügen (`/feature <beschreibung>`)
- Analysiere die bestehende Codebasis
- Plane die Implementierung des gewünschten Features
- Implementiere das Feature mit minimalem Eingriff in bestehenden Code
- Teste die Integration mit vorhandenen Funktionen

### 4. Formular-Generator (`/formular <felder>`)
- Erstelle ein vollständiges Formular mit Validierung
- Unterstütze verschiedene Feldtypen (Text, Zahl, Datum, Dropdown, Checkbox)
- Füge clientseitige Validierung hinzu
- Speichere Daten in localStorage oder bereite API-Anbindung vor

### 5. Dashboard-Widget (`/widget <typ>`)
- Erstelle Dashboard-Widgets (Statistiken, Diagramme, Listen, Statusanzeigen)
- Verwende SVG oder Canvas für Visualisierungen
- Binde Echtzeit-Daten aus localStorage an
- Responsive Design für verschiedene Bildschirmgrößen

## Regeln

- Verwende immer das bestehende Design-System (Dark Theme, Farben aus `:root`)
- Generierter Code muss ohne Build-Tools funktionieren (Vanilla HTML/CSS/JS)
- Alle Texte auf Deutsch
- Barrierefreiheit beachten (ARIA-Labels, semantisches HTML)
