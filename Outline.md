##  Jenga Projektübesicht 🚀

### Frontend

- **PWA:** Die Anwendung soll als Progressive Web App entwickelt werden.
- **Framework:** **SolidJS**

***

### Backend 

- **Frameworks:** **Spring Boot** oder **Quarkus** (Voraussetzung: Unterstützung für OpenAPI).
- **Datenbank:** **PostgreSQL** in Kombination mit **JPA** (Java Persistence API).

***

### Features 

- **Benutzerverwaltung:** Rollen, Gruppen und projektbasierte Berechtigungen.
- **Ticket-System:** Flexibles Vorlagensystem für Jira-ähnliche Tickets.
- **KI-Anbindung:**
    - Automatische Ticketerstellung aus Texten.
    - Intelligente Zusammenfassungen von Kommentaren und Beschreibungen.
- **Versionskontrolle:** Anbindung an **GitHub** zur Verknüpfung von Commits und Pull Requests mit Tickets.

***

### Architektur & DevOps

- **Deployment:** Die gesamte Anwendung wird über **Docker** containerisiert.
- **Authentifizierung:** **Keycloak** für die zentrale Benutzerregistrierung und -verwaltung.
- **Sicherheit:** Absicherung der Verbindung über **Let's Encrypt** SSL-Zertifikate.
- **CI/CD:**
    - Automatisierung von Builds und Tests.
    - Deployment-Pipeline via **GitHub Actions**.
