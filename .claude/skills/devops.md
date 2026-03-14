# DevOps Skill

Dieses Skill-Set erweitert Claude um DevOps-Fähigkeiten für Deployment, Containerisierung und Monitoring.

## Fähigkeiten

### 1. Docker einrichten (`/docker`)
- Erstelle ein `Dockerfile` für die Anwendung (nginx-basiert für statische Dateien)
- Erstelle `docker-compose.yml` mit optionalen Services (DB, Reverse Proxy)
- Generiere `.dockerignore`
- Erstelle Build- und Run-Befehle

### 2. CI/CD Pipeline (`/cicd`)
- Erstelle GitHub Actions Workflow (`.github/workflows/`)
- Automatisches Testen bei Push/PR
- Automatisches Deployment (GitHub Pages, Docker Registry, Server)
- Linting und Qualitätsprüfung als Pipeline-Schritt

### 3. Monitoring & Logging (`/monitoring`)
- Füge clientseitiges Error-Logging hinzu
- Erstelle eine einfache Statusseite
- Performance-Metriken erfassen (Ladezeit, Interaktionszeit)
- Health-Check-Endpunkt vorbereiten

### 4. Backup-Strategie (`/backup`)
- Erstelle ein Backup-Skript für localStorage-Daten
- Automatischer Export als JSON/CSV in regelmäßigen Abständen
- Daten-Wiederherstellung aus Backup-Dateien
- Versionierung der Datenstruktur

### 5. Umgebungskonfiguration (`/env`)
- Erstelle Konfigurationsdateien für verschiedene Umgebungen (Dev, Staging, Prod)
- Umgebungsvariablen-Management
- Feature-Flags für verschiedene Umgebungen
- Server-Konfiguration (nginx, Apache)

## Regeln

- Alle generierten Konfigurationen müssen produktionsreif sein
- Sicherheits-Best-Practices beachten (keine Secrets in Dateien)
- Dokumentation für jeden generierten DevOps-Schritt
- Kompatibilität mit gängigen Hosting-Anbietern sicherstellen
