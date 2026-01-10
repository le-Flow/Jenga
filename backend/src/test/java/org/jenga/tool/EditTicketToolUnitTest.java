package org.jenga.tool;

import jakarta.ws.rs.NotFoundException;
import org.jenga.dto.TicketRequestDTO;
import org.jenga.dto.TicketResponseDTO;
import org.jenga.model.TicketStatus;
import org.jenga.service.TicketService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditTicketToolUnitTest {

    @Mock
    TicketService ticketService;

    @Mock
    ChatRequestContext requestContext;

    @InjectMocks
    EditTicketTool editTicketTool;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEditTicket_Success() {
        Long ticketId = 10L;
        String newTitle = "Updated Title";

        TicketResponseDTO existing = new TicketResponseDTO();
        existing.setId(ticketId);
        existing.setTicketNumber(100L);
        existing.setTitle("Old Title");
        existing.setStatus(TicketStatus.OPEN);

        TicketResponseDTO updated = new TicketResponseDTO();
        updated.setId(ticketId);
        updated.setTicketNumber(100L);
        updated.setTitle(newTitle);

        when(ticketService.findById(ticketId)).thenReturn(existing);
        when(ticketService.update(eq(ticketId), any(TicketRequestDTO.class))).thenReturn(updated);

        // Edit Title only
        String result = editTicketTool.editTicket(ticketId, newTitle, null, null, null, null, null, null);

        assertTrue(result.contains("SUCCESS"));

        ArgumentCaptor<TicketRequestDTO> captor = ArgumentCaptor.forClass(TicketRequestDTO.class);
        verify(ticketService).update(eq(ticketId), captor.capture());

        TicketRequestDTO payload = captor.getValue();
        assertEquals("Updated Title", payload.getTitle());
        assertEquals("Old Title", existing.getTitle());
        assertEquals(TicketStatus.OPEN, payload.getStatus());
    }

    @Test
    void testEditTicket_ContextId() {
        Long contextId = 20L;
        when(requestContext.getCurrentTicketID()).thenReturn(contextId);

        TicketResponseDTO existing = new TicketResponseDTO();
        existing.setId(contextId);
        existing.setTicketNumber(200L);

        when(ticketService.findById(contextId)).thenReturn(existing);
        when(ticketService.update(eq(contextId), any())).thenReturn(existing); // Return same for mock

        editTicketTool.editTicket(null, "New Title", null, null, null, null, null, null);

        verify(ticketService).findById(contextId);
    }

    @Test
    void testEditTicket_NotFound() {
        Long ticketId = 99L;
        when(ticketService.findById(ticketId)).thenThrow(new NotFoundException("Ticket not found"));

        String result = editTicketTool.editTicket(ticketId, "Title", null, null, null, null, null, null);

        assertTrue(result.startsWith("ERROR"));
        assertTrue(result.contains("not found"));
    }
}
