package org.jenga.tool;

import jakarta.ws.rs.NotFoundException;
import org.jenga.dto.TicketResponseDTO;
import org.jenga.service.TicketService;
import org.jenga.service.mcpserver.ChatRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DeleteTicketToolUnitTest {

    @Mock
    TicketService ticketService;

    @Mock
    ChatRequestContext requestContext;

    @InjectMocks
    DeleteTicketTool deleteTicketTool;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteTicket_ExplicitId_Success() {
        Long ticketId = 101L;

        TicketResponseDTO ticket = new TicketResponseDTO();
        ticket.setId(ticketId);
        ticket.setTicketNumber(123L);

        when(ticketService.findById(ticketId)).thenReturn(ticket);

        String result = deleteTicketTool.deleteTicket(ticketId);

        assertTrue(result.contains("SUCCESS"));
        assertTrue(result.contains("123"));
        verify(ticketService).delete(ticketId);
    }

    @Test
    void testDeleteTicket_ContextId_Success() {
        Long contextId = 202L;
        when(requestContext.getCurrentTicketID()).thenReturn(contextId);

        TicketResponseDTO ticket = new TicketResponseDTO();
        ticket.setId(contextId);
        ticket.setTicketNumber(456L);

        when(ticketService.findById(contextId)).thenReturn(ticket);

        String result = deleteTicketTool.deleteTicket(null);

        assertTrue(result.contains("SUCCESS"));
        assertTrue(result.contains("456"));
        verify(ticketService).delete(contextId);
    }

    @Test
    void testDeleteTicket_NoIdNoContext_Error() {
        when(requestContext.getCurrentTicketID()).thenReturn(null);

        String result = deleteTicketTool.deleteTicket(null);

        assertTrue(result.startsWith("ERROR"));
        assertTrue(result.contains("No ticket ID was provided"));
        verify(ticketService, never()).delete(anyLong());
    }

    @Test
    void testDeleteTicket_NotFound() {
        Long ticketId = 999L;
        when(ticketService.findById(ticketId)).thenThrow(new NotFoundException("Ticket not found"));

        String result = deleteTicketTool.deleteTicket(ticketId);

        assertTrue(result.startsWith("ERROR"));
        assertTrue(result.contains("No ticket found with ID: 999"));
    }
}
