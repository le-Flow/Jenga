package org.jenga.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import org.jenga.dto.TicketRequestDTO;
import org.jenga.dto.TicketResponseDTO;
import org.jenga.model.TicketPriority;
import org.jenga.model.TicketSize;
import org.jenga.model.TicketStatus;
import org.jenga.service.TicketService; 

import java.util.List;

@ApplicationScoped
public class CreateTicketTool {

    @Inject
    TicketService ticketService;

    @Tool("Creates a new software development ticket (e.g., bug, feature, task) in the specified project.")
    public String createTicket(
            @P("The title or summary of the ticket. This is mandatory.") 
            String title,
            
            @P("A detailed description of the ticket, including steps to reproduce for bugs. This is mandatory.") 
            String description,
            
            @P("The project ID (e.g., 'MCP', 'Frontend'). This is mandatory.") 
            String projectName,
            
            @P("The username of the person to assign the ticket to. Can be null or 'unassigned'.") 
            String assignee,
            
            @P("The priority of the ticket. Valid values are LOW, MEDIUM, HIGH, CRITICAL.") 
            TicketPriority priority,
            
            @P("The estimated size or effort. Valid values are SMALL, MEDIUM, LARGE, EXTRA_LARGE.") 
            TicketSize size,

            @P("A list of label names to add to the ticket, e.g., ['bug', 'ui', 'backend'].")
            List<String> labels
    ) {
        TicketRequestDTO newTicketDTO = new TicketRequestDTO();
        newTicketDTO.setTitle(title);
        newTicketDTO.setDescription(description);
        newTicketDTO.setProjectName(projectName);
        
        if (assignee != null && !assignee.equalsIgnoreCase("unassigned")) {
            newTicketDTO.setAssignee(assignee);
        }
        if (priority != null) {
            newTicketDTO.setPriority(priority);
        }
        if (size != null) {
            newTicketDTO.setSize(size);
        }
        if (labels != null && !labels.isEmpty()) {
            newTicketDTO.setLabels(labels);
        }
        
        newTicketDTO.setStatus(TicketStatus.OPEN); 
        try {
            TicketResponseDTO createdTicket = ticketService.create(projectName, newTicketDTO);
            
            return "SUCCESS: Created new ticket " +
                   createdTicket.getProjectName() + "-" + createdTicket.getTicketNumber() +
                   ": '" + createdTicket.getTitle() + "'.";

        } catch (NotFoundException | BadRequestException e) {
            return "ERROR: Could not create ticket. Reason: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: An unexpected error occurred: " + e.getMessage();
        }
    }
}