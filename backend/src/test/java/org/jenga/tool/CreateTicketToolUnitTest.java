package org.jenga.tool;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.jenga.db.LabelRepository;
import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.db.UserRepository;
import org.jenga.model.*;
import org.jenga.service.mcpserver.ChatRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreateTicketToolUnitTest {

    @Mock
    TicketRepository ticketRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    LabelRepository labelRepository;

    @Mock
    ChatRequestContext requestContext;

    @InjectMocks
    CreateTicketTool createTicketTool;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTicket_Success() {
        String title = "New Ticket";
        String description = "Desc";
        Long projectId = 100L;
        TicketPriority priority = TicketPriority.HIGH;
        TicketSize size = TicketSize.MEDIUM;
        String assignee = "alice";
        String labels = "bug";
        String criteria = "works";

        Project project = new Project();
        project.setId("PROJ");

        // requestContext.getCurrentUser returns String username
        when(requestContext.getCurrentUser()).thenReturn("currentUser");

        User assigneeUser = new User();
        assigneeUser.setUsername("alice");

        doReturn(project).when(projectRepository).findById(any(Long.class));
        when(userRepository.findByUsername("alice")).thenReturn(assigneeUser);

        // Mocking Panache Query logic for labels
        @SuppressWarnings("unchecked")
        PanacheQuery<Label> query = mock(PanacheQuery.class);
        when(labelRepository.find(anyString(), any(Object.class), any(Object.class))).thenReturn(query);
        Label label = new Label();
        label.setName("bug");
        when(query.firstResult()).thenReturn(label);

        when(ticketRepository.findMaxTicketNumberByProject(project)).thenReturn(5L);

        String result = createTicketTool.createTicket(title, description, projectId, null, assignee, priority, size,
                labels, criteria);

        assertTrue(result.contains("SUCCESS"));

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).persist(ticketCaptor.capture());

        Ticket saved = ticketCaptor.getValue();
        assertEquals("New Ticket", saved.getTitle());
        assertEquals(TicketPriority.HIGH, saved.getPriority());
        assertEquals(TicketSize.MEDIUM, saved.getSize());
        assertEquals("alice", saved.getAssignee().getUsername());
        assertEquals(6L, saved.getTicketNumber());
    }

    @Test
    void testCreateTicket_ProjectNotFound() {
        doReturn(null).when(projectRepository).findById(any(Long.class));

        String result = createTicketTool.createTicket("Title", "Desc", 999L, null, null, TicketPriority.LOW,
                TicketSize.SMALL, null, null);

        assertTrue(result.startsWith("ERROR"));
        assertTrue(result.contains("Project not found"));
    }

    @Test
    void testCreateTicket_AssigneeNotFound_Ignored() {
        Project project = new Project();
        doReturn(project).when(projectRepository).findById(any(Long.class));

        when(requestContext.getCurrentUser()).thenReturn("currentUser");
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        String result = createTicketTool.createTicket("Title", "Desc", 1L, null, "ghost", TicketPriority.LOW,
                TicketSize.SMALL, null, null);

        assertTrue(result.startsWith("SUCCESS"));
    }

    @Test
    void testCreateTicket_UnassignedAssignee_DefaultsToCurrentUser() {
        Project project = new Project();
        project.setId("PROJ");
        doReturn(project).when(projectRepository).findById(any(Long.class));

        // Current user is "currentUser"
        when(requestContext.getCurrentUser()).thenReturn("currentUser");

        // "currentUser" exists in DB
        User currentUserEntity = new User();
        currentUserEntity.setUsername("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(currentUserEntity);

        // "unassigned" should be ignored and default to "currentUser"
        String assigneeInput = "unassigned";

        when(ticketRepository.findMaxTicketNumberByProject(project)).thenReturn(1L);

        String result = createTicketTool.createTicket("Title", "Desc", 1L, null, assigneeInput, TicketPriority.LOW,
                TicketSize.SMALL, null, null);

        assertTrue(result.startsWith("SUCCESS"));

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).persist(ticketCaptor.capture());
        Ticket saved = ticketCaptor.getValue();

        assertEquals("currentUser", saved.getAssignee().getUsername());
    }
}
