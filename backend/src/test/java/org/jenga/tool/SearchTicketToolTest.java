// Integration test here, since this Tool also works as a mapper
package org.jenga.tool;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.db.UserRepository;
import org.jenga.dto.TicketResponseDTO;
import org.jenga.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class SearchTicketToolTest {

    @Inject
    SearchTicketTool searchTicketTool;

    @Inject
    TicketRepository ticketRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    UserRepository userRepository;

    @BeforeEach
    @Transactional
    void setup() {
        // Clean up join tables and children manually to avoid constraint violations
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM ticket_labels").executeUpdate();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM tickets_related").executeUpdate();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM tickets_blocked").executeUpdate();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM acceptance_criterias").executeUpdate();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM comments").executeUpdate();

        ticketRepository.deleteAll();
        ticketRepository.getEntityManager().createNativeQuery("DELETE FROM labels").executeUpdate();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        // Setup Data
        Project project = new Project();
        project.setId("SEARCH");
        project.setName("Search Project");
        projectRepository.persist(project);

        User user = new User();
        user.setUsername("searcher");
        user.setEmail("s@jenga.org");
        user.setPassword("pw");
        userRepository.persist(user);

        // Ticket 1: Match everything
        Ticket t1 = new Ticket();
        t1.setProject(project);
        t1.setTicketNumber(1L);
        t1.setTitle("Database Connection Error");
        t1.setDescription("Cannot connect to DB");
        t1.setStatus(TicketStatus.OPEN);
        t1.setPriority(TicketPriority.CRITICAL);
        t1.setReporter(user);
        t1.setAssignee(user);
        ticketRepository.persist(t1);

        // Ticket 2: Match nothing
        Ticket t2 = new Ticket();
        t2.setProject(project);
        t2.setTicketNumber(2L);
        t2.setTitle("UI Glitch");
        t2.setDescription("CSS wrong");
        t2.setStatus(TicketStatus.CLOSED);
        t2.setPriority(TicketPriority.LOW);
        t2.setReporter(user);
        ticketRepository.persist(t2);
    }

    @Test
    @TestSecurity(user = "searcher", roles = "user")
    void testSearch_Query() {
        List<TicketResponseDTO> results = searchTicketTool.searchTickets(
                "Database", null, null, null, null, null, null, null, null, null, 10);

        assertEquals(1, results.size());
        assertEquals("Database Connection Error", results.get(0).getTitle());
    }

    @Test
    @TestSecurity(user = "searcher", roles = "user")
    void testSearch_Status() {
        List<TicketResponseDTO> results = searchTicketTool.searchTickets(
                null, null, null, null, Collections.singletonList(TicketStatus.CLOSED), null, null, null, null, null,
                10);

        assertEquals(1, results.size());
        assertEquals("UI Glitch", results.get(0).getTitle());
    }

    @Test
    @TestSecurity(user = "searcher", roles = "user")
    void testSearch_Priority() {
        List<TicketResponseDTO> results = searchTicketTool.searchTickets(
                null, null, null, Collections.singletonList(TicketPriority.CRITICAL), null, null, null, null, null,
                null, 10);

        assertEquals(1, results.size());
        assertEquals("Database Connection Error", results.get(0).getTitle());
    }

    @Test
    @TestSecurity(user = "searcher", roles = "user")
    void testSearch_Assignee() {
        List<TicketResponseDTO> results = searchTicketTool.searchTickets(
                null, null, null, null, null, null, Collections.singletonList("searcher"), null, null, null, 10);

        assertEquals(1, results.size());
        assertEquals("Database Connection Error", results.get(0).getTitle());
    }
}
