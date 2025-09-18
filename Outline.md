## Jenga: Projektübersicht 

### \<span style="color:red;"\>Frontend\</span\> 

  - \<span style="color:red;"\>PWA\</span\>
  - \<span style="color:red;"\>Framework: \<u\>SolidJS\</u\>\</span\>

-----

### \<span style="color:blue;"\>Backend\</span\> 

  - \<span style="color:blue;"\>Framework: Springboot, \<u\>Quarkus\</u\> (sollte \<u\>openapi\</u\> haben)\</span\>
  - \<span style="color:blue;"\>Datenbank: Postgres + \<u\>JPA\</u\>\</span\>

-----

### Features 

  - **Benutzerverwaltung:** Rollen, Gruppen und projektbasierte Berechtigungen.
  - **Ticket-System:** Flexibles Vorlagensystem für Jira-ähnliche Tickets.
  - \<span style="color:green;"\>**KI-Anbindung:** Automatische Ticketerstellung, \<u\>Zusammenfassung\</u\> etc.\</span\>
  - \<span style="color:green;"\>**Versionskontrolle:** Github Anbindung\</span\>

-----

### Architektur & DevOps

  - **Deployment:** Die gesamte Anwendung wird über **Docker** containerisiert.
  - **Authentifizierung:** **Keycloak** für die zentrale Benutzerregistrierung und -verwaltung.
  - **Sicherheit:** Absicherung der Verbindung über **\<span style="color:red;"\>\<u\>letsencrypt\</u\>\</span\>** SSL-Zertifikate.
  - **CI/CD:** Deployment-Pipeline via **Github \<u\>Actions\</u\>** (falls zeitlich passend).
