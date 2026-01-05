package org.jenga.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.db.UserRepository;
import org.jenga.dto.GitHubIssueDTO;
import org.jenga.dto.ImportReportDTO;
import org.jenga.dto.TicketRequestDTO;
import org.jenga.model.Project;
import org.jenga.model.Ticket;
import org.jenga.model.TicketStatus;
import org.jenga.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ImportResourceTest {

    @io.quarkus.test.InjectMock
    org.jenga.service.AuthenticationService authenticationService;

    @Inject
    ImportResource importResource;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    TicketRepository ticketRepository;

    @Inject
    UserRepository userRepository;

    private final String projectId = "test-project-import";
    private final String username = "testuser";

    @BeforeEach
    @Transactional
    void setup() {
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM ticket_labels").executeUpdate();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM tickets_related").executeUpdate();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM tickets_blocked").executeUpdate();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM acceptance_criterias").executeUpdate();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM comments").executeUpdate();

        ticketRepository.deleteAll();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM labels").executeUpdate();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername(username);
        user.setEmail("test@jenga.org");
        user.setPassword("password");
        userRepository.persist(user);

        Project project = new Project();
        project.setId(projectId);
        project.setName("Import Project");
        project.setDescription("Project for import tests");
        projectRepository.persist(project);
    }

    @Test
    void testImportFromJenga() {
        org.mockito.Mockito.when(authenticationService.getCurrentUser())
                .thenReturn(userRepository.findByUsername(username));

        TicketRequestDTO request = new TicketRequestDTO();
        request.setTitle("Imported Jenga Ticket");
        request.setDescription("Description from Jenga");
        request.setStatus(TicketStatus.OPEN);
        request.setReporter(username);
        request.setProjectName(projectId);

        List<TicketRequestDTO> requests = Collections.singletonList(request);

        Response response = importResource.importFromJenga(projectId, requests);

        assertEquals(200, response.getStatus());
        ImportReportDTO report = (ImportReportDTO) response.getEntity();
        assertEquals(1, report.getSuccessfulImportCount());
        assertTrue(report.getFailedImports().isEmpty());

        List<Ticket> tickets = ticketRepository.listAll();
        assertEquals(1, tickets.size());
        assertEquals("Imported Jenga Ticket", tickets.get(0).getTitle());
    }

    @Test
    @TestSecurity(user = "testuser", roles = "user")
    void testImportFromGitHub() {
        org.mockito.Mockito.when(authenticationService.getCurrentUser())
                .thenReturn(userRepository.findByUsername(username));

        GitHubIssueDTO issue = new GitHubIssueDTO();
        issue.setTitle("Imported GitHub Issue");
        issue.setBody("GitHub body content");

        GitHubIssueDTO.GitHubProjectStatusDTO statusDto = new GitHubIssueDTO.GitHubProjectStatusDTO();
        statusDto.setName("OPEN");
        GitHubIssueDTO.GitHubProjectItemDTO itemDto = new GitHubIssueDTO.GitHubProjectItemDTO();
        itemDto.setStatus(statusDto);
        issue.setStatus(new GitHubIssueDTO.GitHubProjectItemDTO[] { itemDto });

        List<GitHubIssueDTO> issues = Collections.singletonList(issue);

        Response response = importResource.importFromGitHub(projectId, issues);

        assertEquals(200, response.getStatus());
        ImportReportDTO report = (ImportReportDTO) response.getEntity();
        assertEquals(1, report.getSuccessfulImportCount());
        assertTrue(report.getFailedImports().isEmpty());

        List<Ticket> tickets = ticketRepository.listAll();
        assertEquals(1, tickets.size());
        assertEquals("Imported GitHub Issue", tickets.get(0).getTitle());
        assertEquals(username, tickets.get(0).getReporter().getUsername());
    }
}
