package org.jenga.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jenga.dto.CreateTicketDTO;
import org.jenga.dto.GitHubIssueDTO;
import org.jenga.model.Ticket;
import org.jenga.model.TicketStatus;
import org.jenga.service.TicketService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImportResource {

    @Inject 
    TicketService ticketService;

    @POST
    @Path("/{projectId}/github")
    public Response importFromGitHub(
                @PathParam("projectId") String projectId, 
                List<GitHubIssueDTO> githubIssues) { 
        
        List<Ticket> createdTickets = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (GitHubIssueDTO githubDto : githubIssues) {
            try {                CreateTicketDTO internalDto = mapGitHubToInternal(githubDto);
                Ticket newTicket = ticketService.create(projectId, internalDto);
                createdTickets.add(newTicket);

            } catch (Exception e) {
                errors.add("Failed to import ticket '" + githubDto.getTitle() + "': " + e.getMessage());
            }
        }
        return Response.ok(new ImportReport(createdTickets, errors)).build();
    }

    private CreateTicketDTO mapGitHubToInternal(GitHubIssueDTO githubDto) {
        CreateTicketDTO dto = new CreateTicketDTO();

        dto.setTitle(githubDto.getTitle());
        dto.setDescription(githubDto.getBody()); // TODO: Parse individual parameters (Body = Specification + Acceptance Criteria + Relationships), blocked by implementation in Ticket

        if (githubDto.getAssignees() != null && githubDto.getAssignees().length > 0) {
            dto.setAssignee(githubDto.getAssignees()[0].getLogin());
        }

        if (githubDto.getStatus() != null && githubDto.getStatus().length > 0 &&
            githubDto.getStatus()[0].getStatus() != null) {
            
            String githubStatusName = githubDto.getStatus()[0].getStatus().getName();
            
            TicketStatus mappedStatus;

            switch (githubStatusName.toUpperCase()) {
                case "OPEN":
                case "BACKLOG":
                case "NO STATUS":
                    mappedStatus = TicketStatus.OPEN;
                    break;
                case "IN PROGRESS": 
                    mappedStatus = TicketStatus.IN_PROGRESS;
                    break;
                case "IN REVIEW":
                case "IN TEST":
                    mappedStatus = TicketStatus.IN_REVIEW;
                    break;
                case "RESOLVED":
                    mappedStatus = TicketStatus.RESOLVED;
                    break;
                case "CLOSED":
                case "DONE":
                    mappedStatus = TicketStatus.CLOSED;
                    break;
                case "ON HOLD":
                    mappedStatus = TicketStatus.ON_HOLD;
                    break;
                default:
                    System.err.println("Warning: Unrecognized status '" + githubStatusName + "'. Defaulting to OPEN.");
                    mappedStatus = TicketStatus.OPEN; 
                    break;
            }
            
            dto.setStatus(mappedStatus); 
        }

        if (githubDto.getLabels() != null) {
            List<String> labelNames = Arrays.stream(githubDto.getLabels())
                                            .map(GitHubIssueDTO.GitHubLabelDTO::getName)
                                            .collect(Collectors.toList());

            dto.setLabels(labelNames);
        }

        return dto;
    }

    public static class ImportReport {
        public List<Ticket> successfulImports;
        public List<String> failedImports;
        public ImportReport(List<Ticket> s, List<String> f) {
            this.successfulImports = s;
            this.failedImports = f;
        }
    }
}