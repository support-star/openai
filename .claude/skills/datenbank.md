# Datenbank Skill

Dieses Skill-Set erweitert Claude um Datenbank-Design und -Verwaltungs-Fähigkeiten.

## Fähigkeiten

### 1. Datenbank-Design (`/db-design`)
- Datenbankschema entwerfen (relational, NoSQL)
- Tabellen und Beziehungen definieren (1:1, 1:n, n:m)
- Normalisierung durchführen (1NF bis 3NF)
- ER-Diagramm als SVG/Text generieren
- Indexierung und Performance-Optimierung planen

### 2. SQL-Generierung (`/sql <aktion>`)
- CREATE TABLE Statements generieren
- SELECT/INSERT/UPDATE/DELETE Queries erstellen
- Views und Stored Procedures schreiben
- Migrationen erstellen (Schema-Änderungen)
- Datenbank-Seed-Skripte generieren

### 3. localStorage-Verwaltung (`/storage`)
- localStorage-Struktur optimieren
- Daten migrieren zwischen Versionen
- Speicherplatz analysieren und bereinigen
- Import/Export-Funktionen erstellen
- IndexedDB als Alternative implementieren

### 4. Backend-Vorbereitung (`/backend <typ>`)
- REST-API-Endpunkte definieren (OpenAPI/Swagger)
- Datenbankanbindung vorbereiten (SQLite, PostgreSQL, MySQL)
- ORM-Modelle generieren
- API-Routen mit CRUD-Operationen
- Authentifizierung und Autorisierung einplanen

### 5. Datenmigration (`/migration`)
- Migrationsskripte erstellen (localStorage → Datenbank)
- Datenvalidierung und -bereinigung
- Backup vor Migration erstellen
- Rollback-Strategie definieren
- Datenintegrität prüfen

## Regeln
- Datenintegrität immer sicherstellen
- Backup vor jeder Strukturänderung
- DSGVO-konforme Datenhaltung
- Performance bei großen Datenmengen berücksichtigen
- SQL-Injection verhindern (Prepared Statements)
