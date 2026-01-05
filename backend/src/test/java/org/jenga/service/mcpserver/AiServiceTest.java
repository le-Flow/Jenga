package org.jenga.service.mcpserver;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.db.UserRepository;
import org.jenga.model.Project;
import org.jenga.model.Ticket;
import org.jenga.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
public class AiServiceTest {

    @InjectMock
    AiService aiService;

    @Inject
    ChatRequestContext requestContext;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TicketRepository ticketRepository;

    private final String projectId = "100";
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
        project.setName("Integration Test Project");
        project.setDescription("A project for integration testing");
        projectRepository.persist(project);
    }

    @Test
    void testChat_CreateTicket_Workflow_Mocked() {
        requestContext.setCurrentUser(username);
        requestContext.setCurrentProjectID(Long.parseLong(projectId));

        String conversationId = "test-conv-mock";

        Mockito.when(aiService.chat(eq(conversationId), anyString()))
                .thenAnswer(invocation -> {
                    createMockTicketInDb();
                    return "I have successfully created the ticket 'Test Bug'.";
                });

        String prompt = "Create a bug ticket...";
        String response = aiService.chat(conversationId, prompt);

        assertFalse(response.isBlank());

        List<Ticket> tickets = ticketRepository.listAll();
        assertFalse(tickets.isEmpty(), "Ticket should be present (simulated by mock)");

        Ticket ticket = tickets.get(0);
        assertEquals("Test Bug", ticket.getTitle());
        assertEquals("Integration Test Project", ticket.getProject().getName());
        assertEquals(username, ticket.getReporter().getUsername());
    }

    @Transactional
    void createMockTicketInDb() {
        Project p = projectRepository.findById(projectId);
        User u = userRepository.findByUsername(username);

        Ticket t = new Ticket();
        t.setTitle("Test Bug");
        t.setDescription("Integration test bug description");
        t.setProject(p);
        t.setReporter(u);
        ticketRepository.persist(t);
    }
}