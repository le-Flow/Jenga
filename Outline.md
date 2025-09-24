## Projektskizze: „Jenga“ – Ein KI-gestütztes Projektmanagement-Tool

**Jenga** ist ein Projektmanagement- und Issue-Tracking-Tool, das einen integrierten KI-Agenten als smarten Assistenten zur Unterstützung und zur Automatisierung von Aufgaben bietet.

### 1\. Kernkomponenten & Technologie-Stack

  * **Frontend** `(Verantwortlich: Jonas)`

      * **Framework:** **SolidJS** für ein reaktives und performantes Nutzererlebnis.
      * **Plattform:** Entwicklung als **Progressive Web App (PWA)** für eine nahtlose Desktop- und Mobilnutzung.

  * **Backend** `(Verantwortlich: Dyonis)`

      * **Framework:** **Quarkus** für schnelle Startzeiten und einen geringen Ressourcenverbrauch (Cloud-Native).
      * **API:** Generierung einer **OpenAPI-Spezifikation** zur klaren Definition der Schnittstellen.
      * **Datenbank:** **PostgreSQL** in Kombination mit **JPA (Java Persistence API)** für eine robuste und standardkonforme Datenpersistenz.

### 2\. Geplante Features

  * **Benutzer- & Rechteverwaltung**

      * Implementierung von Rollen, Gruppen und projektbasierten Berechtigungen zur feingranularen Zugriffskontrolle.

  * **Flexibles Ticket-System**

      * Ein anpassbares Vorlagensystem für Tickets (vergleichbar mit Jira), um verschiedene Projekt-Workflows abzubilden.

  * **KI-Integration** `(Verantwortlich: Adam)`

      * **Automatische Ticketerstellung:** Analyse von eingehenden Texten (z.B. E-Mails), um automatisch Tickets zu erstellen und zu kategorisieren.
      * **Intelligente Zusammenfassungen:** KI-gestützte Generierung von prägnanten Zusammenfassungen langer Kommentarthreads und Ticketbeschreibungen.

  * **Versionskontrolle & GitHub-Anbindung** `(Verantwortlich: Adam)`

      * Verknüpfung von GitHub Commits und Pull Requests direkt mit den entsprechenden Tickets im System.

### 3\. Architektur & DevOps-Konzepte

  * **Deployment & Containerisierung**

      * Die gesamte Anwendung (Frontend, Backend, Datenbank) wird vollständig über **Docker** containerisiert, um eine konsistente und portable Umgebung zu gewährleisten.

  * **Authentifizierung & Sicherheit**

      * Einsatz von **Keycloak** als zentraler Identity-Provider für die Benutzerregistrierung, -anmeldung und -verwaltung via OAuth.
      * Absicherung der Web-Verbindung durch **Let's Encrypt SSL-Zertifikate**.

  * **CI/CD-Pipeline**

      * Einrichtung einer automatisierten Build-, Test- und Deployment-Pipeline mithilfe von **GitHub Actions**.