package org.jenga.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jenga.dto.TicketResponseDTO;
import org.jenga.dto.MCP_Server.AskAboutTicketResponseDTO;
import org.jenga.service.TicketService;

@ApplicationScoped 
public class AskAboutTicketTool {

    @Inject
    TicketService ticketService; 

    @Tool("Get information about a specific ticket by its project ID and ticket ID")
    
    public AskAboutTicketResponseDTO getTicketInfo(
            @P("The project ID or key, e.g., 'PROJ' or 'ZEN'") String projectId,
            @P("The numerical ID of the ticket") Long ticketId) {

        TicketResponseDTO ticket = ticketService.findById(projectId, ticketId);

        if (ticket == null) {
            return new AskAboutTicketResponseDTO(
                "Sorry, I couldn't find a ticket with ID " + projectId + "-" + ticketId,
                ticketId.toString()
            );
        }

        String message = String.format(
            "Ticket %s-%d: '%s' (%s). Assigned to: %s. Description: %s",
            ticket.getProjectName(),
            ticket.getId(),
            ticket.getTitle(),
            ticket.getStatus(),
            ticket.getAssignee(),
            ticket.getDescription()
        );

        /*
         *  ticket.getId(),
            ticket.getTicketNumber(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getProjectName(),
            ticket.getPriority(),
            ticket.getSize(),
            ticket.getStatus(),
            ticket.getCreateDate(),
            ticket.getModifyDate(),
            ticket.getReporterName(),
            ticket.getAssigneeName(),
            ticket.getLabels(),
            ticket.getAcceptanceCriteria()
         */

        return new AskAboutTicketResponseDTO(message, ticket.getId().toString());
    }
}

