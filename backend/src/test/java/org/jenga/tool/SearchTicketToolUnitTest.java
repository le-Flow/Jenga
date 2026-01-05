package org.jenga.tool;

import org.jenga.dto.TicketSearchDTO;
import org.jenga.model.TicketPriority;
import org.jenga.model.TicketStatus;
import org.jenga.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchTicketToolUnitTest {

    @Mock
    TicketService ticketService;

    @InjectMocks
    SearchTicketTool searchTicketTool;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchTickets_Mapping() {
        String query = "bug";
        String title = "Login";
        String projectId = "PROJ";
        List<TicketPriority> priorities = List.of(TicketPriority.HIGH);
        List<TicketStatus> statuses = List.of(TicketStatus.OPEN);
        List<String> assignees = List.of("alice");
        Integer limit = 10;

        when(ticketService.searchTickets(org.mockito.ArgumentMatchers.any())).thenReturn(Collections.emptyList());

        searchTicketTool.searchTickets(query, title, null, priorities, statuses, null, assignees, null, null, projectId,
                limit);

        ArgumentCaptor<TicketSearchDTO> captor = ArgumentCaptor.forClass(TicketSearchDTO.class);
        verify(ticketService).searchTickets(captor.capture());

        TicketSearchDTO captured = captor.getValue();
        assertEquals("bug", captured.getQuery());
        assertEquals("Login", captured.getTitle());
        assertEquals("PROJ", captured.getProjectId());
        assertEquals(10, captured.getLimit());

        assertEquals(1, captured.getFilter().getPriority().size());
        assertEquals(TicketPriority.HIGH, captured.getFilter().getPriority().get(0));

        assertEquals(1, captured.getFilter().getStatus().size());
        assertEquals(TicketStatus.OPEN, captured.getFilter().getStatus().get(0));

        assertEquals(1, captured.getFilter().getAssignee().size());
        assertEquals("alice", captured.getFilter().getAssignee().get(0));
    }

    @Test
    void testSearchTickets_Defaults() {
        when(ticketService.searchTickets(org.mockito.ArgumentMatchers.any())).thenReturn(Collections.emptyList());

        // Call with nulls
        searchTicketTool.searchTickets(null, null, null, null, null, null, null, null, null, null, null);

        ArgumentCaptor<TicketSearchDTO> captor = ArgumentCaptor.forClass(TicketSearchDTO.class);
        verify(ticketService).searchTickets(captor.capture());

        TicketSearchDTO captured = captor.getValue();
        assertEquals(5, captured.getLimit()); // Default check
    }
}
