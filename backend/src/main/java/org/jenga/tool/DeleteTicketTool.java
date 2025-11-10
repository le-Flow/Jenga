package org.jenga.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import org.jenga.dto.TicketResponseDTO;
import org.jenga.service.TicketService;

@ApplicationScoped
public class DeleteTicketTool {

    @Inject
    TicketService ticketService;

    @Tool("Deletes a specific ticket using its project ID and ticket number (e.g., 'MCP', 123).")
    public String deleteTicket(
            @P("The project ID (e.g., 'MCP', 'Frontend') of the ticket to delete. This is mandatory.")
            String projectName,
            
            @P("The ticket number (e.g., 123, 456) of the ticket to delete. This is mandatory.")
            Long ticketNumber
    ) {
        
        try {
            TicketResponseDTO ticket = ticketService.findByTicketNumber(projectName, ticketNumber);

            ticketService.delete(ticket.getId());
            
            return "SUCCESS: Successfully deleted ticket " + projectName + "-" + ticketNumber + ".";

        } catch (NotFoundException e) {
            return "ERROR: Could not delete ticket. Reason: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: An unexpected error occurred while deleting ticket: " + e.getMessage();
        }
    }
}