package org.jenga.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class ImportServiceTest {

    @Inject
    ImportService importService;

    @Inject
    TicketRepository ticketRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    UserRepository userRepository;

    @io.quarkus.test.InjectMock
    AuthenticationService authenticationService;

    private final String projectId = "import-integ-proj";
    private final String username = "importuser";

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
        user.setEmail("import@jenga.org");
        user.setPassword("password");
        userRepository.persist(user);

        Project project = new Project();
        project.setId(projectId);
        project.setName("Import Integration Project");
        projectRepository.persist(project);
    }

    @Test
    @TestSecurity(user = "importuser", roles = "user")
    void testImportFromGitHub_Integration() {
        org.mockito.Mockito.when(authenticationService.getCurrentUser())
                .thenReturn(userRepository.findByUsername(username));

        GitHubIssueDTO issue = new GitHubIssueDTO();
        issue.setTitle("Integration GitHub Issue");
        issue.setBody("Testing integration persistence");

        // Setup label
        GitHubIssueDTO.GitHubLabelDTO label = new GitHubIssueDTO.GitHubLabelDTO();
        label.setName("gh-label");
        label.setColor("ff0000");
        issue.setLabels(new GitHubIssueDTO.GitHubLabelDTO[] { label });

        ImportReportDTO report = importService.importFromGitHub(projectId, Collections.singletonList(issue));

        assertEquals(1, report.getSuccessfulImportCount());

        List<Ticket> tickets = ticketRepository.listAll();
        assertEquals(1, tickets.size());
        Ticket t = tickets.get(0);
        assertEquals("Integration GitHub Issue", t.getTitle());
        assertEquals(username, t.getReporter().getUsername());

        // Verify label persistence
        assertEquals(1, t.getLabels().size());
        assertEquals("gh-label", t.getLabels().get(0).getName());
    }

    @Test
    @TestSecurity(user = "importuser", roles = "user")
    void testImportFromJenga_Integration() {
        org.mockito.Mockito.when(authenticationService.getCurrentUser())
                .thenReturn(userRepository.findByUsername(username));

        TicketRequestDTO request = new TicketRequestDTO();
        request.setTitle("Integration Jenga Issue");
        request.setDescription("Testing integration Jenga");
        request.setProjectName(projectId);
        request.setReporter(username);
        request.setStatus(TicketStatus.OPEN);
        request.setLabels(Collections.singletonList("jenga-label"));

        ImportReportDTO report = importService.importFromJenga(projectId, Collections.singletonList(request));

        assertEquals(1, report.getSuccessfulImportCount());

        List<Ticket> tickets = ticketRepository.listAll();
        assertEquals(1, tickets.size());
        Ticket t = tickets.get(0);
        assertEquals("Integration Jenga Issue", t.getTitle());

        // Verify label persistence (Jenga import also creates labels if missing)
        assertEquals(1, t.getLabels().size());
        assertEquals("jenga-label", t.getLabels().get(0).getName());
    }
}
