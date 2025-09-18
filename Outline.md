##  Jenga: Projektübersicht 

### Frontend 

- **PWA:** Die Anwendung soll als Progressive Web App entwickelt werden. (Jonas)
- **Framework:** **SolidJS** (Jonas)

***

### Backend 

- **Frameworks:** **Spring Boot** oder **Quarkus** (Voraussetzung: Unterstützung für OpenAPI). (Dyonis)
- **Datenbank:** **PostgreSQL** in Kombination mit **JPA** (Java Persistence API). (Dyonis)

***

### Features 

- **Benutzerverwaltung:** Rollen, Gruppen und projektbasierte Berechtigungen.
- **Ticket-System:** Flexibles Vorlagensystem für Jira-ähnliche Tickets.
- **KI-Anbindung:** (Adam)
    - Automatische Ticketerstellung aus Texten.
    - Intelligente Zusammenfassungen von Kommentaren und Beschreibungen.
- **Versionskontrolle:** Anbindung an **GitHub** zur Verknüpfung von Commits und Pull Requests mit Tickets. (Adam)

***

### Architektur & DevOps 

- **Deployment:** Die gesamte Anwendung wird über **Docker** containerisiert.
- **Authentifizierung:** **Keycloak** für die zentrale Benutzerregistrierung und -verwaltung.
- **Sicherheit:** Absicherung der Verbindung über **Let's Encrypt** SSL-Zertifikate.
- **CI/CD:**
    - Automatisierung von Builds und Tests.
    - Deployment-Pipeline via **GitHub Actions**.
