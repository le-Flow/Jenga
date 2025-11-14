package org.jenga.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import org.jenga.dto.TicketResponseDTO;
import org.jenga.service.ProjectService;
import org.jenga.service.TicketService;

@ApplicationScoped
public class DeleteTicketTool {

    @Inject
    TicketService ticketService;

    @Inject 
    ProjectService projectService;

    @Tool("Deletes a specific ticket using its project ID and ticket number (e.g., 'MCP', 123).")
    public String deleteTicket(            
            @P("The ticket ID (e.g., 123, 456) of the ticket to delete. This is mandatory.")
            Long ticketId
    ) {
        
        try {
            TicketResponseDTO ticket = ticketService.findById(ticketId);

            ticketService.delete(ticket.getId());
            
            return "SUCCESS: Successfully deleted ticket" + ticketId + ".";

        } catch (NotFoundException e) {
            return "ERROR: Could not delete ticket. Reason: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: An unexpected error occurred while deleting ticket: " + e.getMessage();
        }
    }
}