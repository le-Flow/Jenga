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
      * **Automatische Zusammenhänge erfassen:** Automatische Zuweisung von Verwandten Tickets
      * **Intelligente Zusammenfassungen:** KI-gestützte Generierung von prägnanten Zusammenfassungen langer Kommentarthreads und Ticketbeschreibungen.
      * **Intelligente Search:** KI-gestützte suche (Keyword erstellung + Fuzzy Search oder RAG)
  * **KI-Integration Architektur** `(Verantwortlich: Adam)`
      * **MPC Server (Model/Pipeline/Compute):** Stellt über APIs dedizierte Inferenz-Endpunkte für die KI-Modelle bereit und ist für das Management der zugrundeliegenden Compute-Ressourcen verantwortlich.
      * **LangChain:** Ein Orchestrierungs-Framework, das die logische Verkettung (Chaining) von LLMs mit anderen Komponenten (z. B. Vektordatenbanken, APIs) zur Erstellung komplexer KI-Applikationen ermöglicht.
      * **LiteLLM:** Eine Abstraktionsschicht, die als Proxy fungiert, um API-Aufrufe an diverse LLM-Anbieter in ein einheitliches Format zu übersetzen und so die Interoperabilität der Modelle sicherstellt.
  * **Versionskontrolle & GitHub-Anbindung** `(Verantwortlich: Adam)`

      * Verknüpfung von GitHub Commits und Pull Requests direkt mit den entsprechenden Tickets im System.

### 3\. Architektur & DevOps-Konzepte

  * **Deployment & Containerisierung** `(Verantwortlich: Lin)`

      * Die gesamte Anwendung (Frontend, Backend, Datenbank) wird vollständig über **Docker** containerisiert, um eine konsistente und portable Umgebung zu gewährleisten.

  * **Authentifizierung & Sicherheit** `(Verantwortlich: Lin)`

      * Einsatz von **Keycloak** als zentraler Identity-Provider für die Benutzerregistrierung, -anmeldung und -verwaltung via OAuth. 
      * Absicherung der Web-Verbindung durch **Let's Encrypt SSL-Zertifikate**.

  * **CI/CD-Pipeline**

      * Einrichtung einer automatisierten Build-, Test- und Deployment-Pipeline mithilfe von **GitHub Actions**. `(Verantwortlich: Lin)`