package org.jenga.tool;

import jakarta.ws.rs.NotFoundException;
import org.jenga.dto.TicketResponseDTO;
import org.jenga.dto.mcpserver.AskAboutTicketResponseDTO;
import org.jenga.model.TicketStatus;
import org.jenga.service.TicketService;
import org.jenga.service.mcpserver.ChatRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class AskAboutTicketToolUnitTest {

    @Mock
    TicketService ticketService;

    @Mock
    ChatRequestContext requestContext;

    @InjectMocks
    AskAboutTicketTool askAboutTicketTool;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTicketInfo_ExplicitId_Success() {
        Long ticketId = 100L;
        TicketResponseDTO mockTicket = new TicketResponseDTO();
        mockTicket.setId(ticketId);
        mockTicket.setTicketNumber(5L);
        mockTicket.setProjectName("PROJ");
        mockTicket.setTitle("Bug Title");
        mockTicket.setStatus(TicketStatus.OPEN);
        mockTicket.setAssignee("bob");
        mockTicket.setDescription("Description text");

        when(ticketService.findById(ticketId)).thenReturn(mockTicket);

        AskAboutTicketResponseDTO response = askAboutTicketTool.getTicketInfo(ticketId);

        assertNotNull(response);
        assertEquals("100", response.getTicketId());
        assertTrue(response.getMessage().contains("Ticket PROJ-5"));
        assertTrue(response.getMessage().contains("Bug Title"));
        assertTrue(response.getMessage().contains("bob"));
    }

    @Test
    void testGetTicketInfo_NullId_UsesContext_Success() {
        Long contextId = 200L;
        when(requestContext.getCurrentTicketID()).thenReturn(contextId);

        TicketResponseDTO mockTicket = new TicketResponseDTO();
        mockTicket.setId(contextId);
        mockTicket.setTicketNumber(10L);
        mockTicket.setProjectName("CTX");
        mockTicket.setTitle("Context Ticket");
        mockTicket.setStatus(TicketStatus.IN_PROGRESS);
        mockTicket.setAssignee(null); // Unassigned
        mockTicket.setDescription("Ctx Desc");

        // Tool calls service with context ID
        when(ticketService.findById(contextId)).thenReturn(mockTicket);

        AskAboutTicketResponseDTO response = askAboutTicketTool.getTicketInfo(null);

        assertNotNull(response);
        assertEquals("200", response.getTicketId());
        assertTrue(response.getMessage().contains("Ticket CTX-10"));
        assertTrue(response.getMessage().contains("Unassigned"));
    }

    @Test
    void testGetTicketInfo_NullId_NoContext_Error() {
        when(requestContext.getCurrentTicketID()).thenReturn(null);

        AskAboutTicketResponseDTO response = askAboutTicketTool.getTicketInfo(null);

        assertNotNull(response);
        assertNull(response.getTicketId());
        assertTrue(response.getMessage().startsWith("ERROR: No ticket ID was provided"));
        verify(ticketService, never()).findById(anyLong());
    }

    @Test
    void testGetTicketInfo_NotFound() {
        Long ticketId = 999L;
        when(ticketService.findById(ticketId)).thenThrow(new NotFoundException("Not found"));

        AskAboutTicketResponseDTO response = askAboutTicketTool.getTicketInfo(ticketId);

        assertNotNull(response);
        assertEquals("999", response.getTicketId());
        assertTrue(response.getMessage().contains("couldn't find a ticket with ID: 999"));
    }

    @Test
    void testGetTicketInfo_GeneralException() {
        Long ticketId = 500L;
        when(ticketService.findById(ticketId)).thenThrow(new RuntimeException("DB Failure"));

        AskAboutTicketResponseDTO response = askAboutTicketTool.getTicketInfo(ticketId);

        assertNotNull(response);
        // On generic error, ID might be null
        assertTrue(response.getMessage().startsWith("ERROR: An unexpected error occurred"));
        assertTrue(response.getMessage().contains("DB Failure"));
    }
}
