package org.jenga.service;

import org.jenga.db.LabelRepository;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ImportServiceUnitTest {

    @Mock
    TicketRepository ticketRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    LabelRepository labelRepository;

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    ImportService importService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testImportFromGitHub_Success() {
        String projectId = "proj1";
        String username = "user1";

        Project project = new Project();
        project.setId(projectId);

        User user = new User();
        user.setUsername(username);

        when(projectRepository.findById(projectId)).thenReturn(project);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(ticketRepository.findMaxTicketNumberByProject(project)).thenReturn(10L);

        GitHubIssueDTO issue = new GitHubIssueDTO();
        issue.setTitle("GH Issue");
        issue.setBody("Body content\n- [ ] Acceptance criteria 1");

        GitHubIssueDTO.GitHubProjectStatusDTO statusDto = new GitHubIssueDTO.GitHubProjectStatusDTO();
        statusDto.setName("IN PROGRESS");
        GitHubIssueDTO.GitHubProjectItemDTO itemDto = new GitHubIssueDTO.GitHubProjectItemDTO();
        itemDto.setStatus(statusDto);
        issue.setStatus(new GitHubIssueDTO.GitHubProjectItemDTO[] { itemDto });

        ImportReportDTO report = importService.importFromGitHub(projectId, Collections.singletonList(issue));

        assertEquals(1, report.getSuccessfulImportCount());

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).persist(ticketCaptor.capture());

        Ticket savedTicket = ticketCaptor.getValue();
        assertEquals("GH Issue", savedTicket.getTitle());
        assertEquals(TicketStatus.IN_PROGRESS, savedTicket.getStatus());
        assertEquals(1, savedTicket.getAcceptanceCriteria().size());
        assertEquals("Acceptance criteria 1", savedTicket.getAcceptanceCriteria().get(0).getDescription());
    }

    @Test
    void testImportFromJenga_Success() {
        String projectId = "proj1";
        String username = "user1";

        Project project = new Project();
        project.setId(projectId);

        User user = new User();
        user.setUsername(username);

        when(projectRepository.findById(projectId)).thenReturn(project);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(ticketRepository.findMaxTicketNumberByProject(project)).thenReturn(5L);

        TicketRequestDTO request = new TicketRequestDTO();
        request.setTitle("Jenga Issue");
        request.setReporter(username);
        request.setProjectName(projectId);
        request.setStatus(TicketStatus.OPEN);

        ImportReportDTO report = importService.importFromJenga(projectId, Collections.singletonList(request));

        assertEquals(1, report.getSuccessfulImportCount());

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).persist(ticketCaptor.capture());

        Ticket savedTicket = ticketCaptor.getValue();
        assertEquals("Jenga Issue", savedTicket.getTitle());
        assertEquals(TicketStatus.OPEN, savedTicket.getStatus());
        assertEquals(project, savedTicket.getProject());
    }
}
