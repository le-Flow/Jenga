package org.jenga.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.db.UserRepository;
import org.jenga.db.LabelRepository;
import org.jenga.dto.GitHubIssueDTO;
import org.jenga.dto.ImportReportDTO;
import org.jenga.model.Label;
import org.jenga.model.Project;
import org.jenga.model.Ticket;
import org.jenga.model.TicketStatus;
import org.jenga.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ImportService {

    @Inject
    TicketRepository ticketRepository;
    @Inject
    ProjectRepository projectRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    LabelRepository labelRepository;
    @Inject
    AuthenticationService authenticationService;

    @Transactional
    public ImportReportDTO importFromGitHub(String projectId, List<GitHubIssueDTO> githubIssues) {
        
        int successfulImportCount = 0;
        List<String> failedImports = new ArrayList<>();

        Project project = projectRepository.findById(projectId);
        if (project == null) {
            failedImports.add("Fatal: Project not found with ID: " + projectId);
            return new ImportReportDTO(0, failedImports);
        }
        
        User reporter = authenticationService.getCurrentUser();

        for (GitHubIssueDTO githubDto : githubIssues) {
            try {
                Ticket ticket = new Ticket();
                ticket.setProject(project);
                ticket.setTitle(githubDto.getTitle());
                ticket.setDescription(githubDto.getBody()); // TODO: Parse individual parameters (Body = Specification + Acceptance Criteria + Relationships), blocked by implementation in Ticket
                ticket.setReporter(reporter);

                if (githubDto.getAssignees() != null && githubDto.getAssignees().length > 0) {
                    String username = githubDto.getAssignees()[0].getLogin();
                    User assignee = userRepository.findByUsername(username);
                    if (assignee != null) {
                        ticket.setAssignee(assignee);
                    } else {
                        System.err.println("Import Warning: Assignee not found, skipping: " + username);
                    }
                }

                if (githubDto.getLabels() != null && githubDto.getLabels().length > 0) {
                    Set<Label> labelsToAttach = new HashSet<>();
                    for (GitHubIssueDTO.GitHubLabelDTO labelDto : githubDto.getLabels()) {
                        String labelName = labelDto.getName();
                        
                        Label label = labelRepository.find("project = ?1 and name = ?2", project, labelName).firstResult();

                        if (label == null) {
                            label = new Label();
                            label.setName(labelName);
                            label.setProject(project);
                            label.setColor(labelDto.getColor());
                            labelRepository.persist(label);
                        }
                        labelsToAttach.add(label);
                    }
                    ticket.setLabels(new ArrayList<>(labelsToAttach));
                }
                
                ticket.setStatus(mapStatusFromDto(githubDto));

                ticketRepository.persist(ticket);
                successfulImportCount++; 

            } catch (Exception e) {
                failedImports.add("Failed to import ticket '" + githubDto.getTitle() + "': " + e.getMessage());
            }
        }
        
        return new ImportReportDTO(successfulImportCount, failedImports);
    }
    
    private TicketStatus mapStatusFromDto(GitHubIssueDTO githubDto) {
        if (githubDto.getStatus() == null || githubDto.getStatus().length == 0 || githubDto.getStatus()[0].getStatus() == null) {
            return TicketStatus.OPEN;
        }
        
        String githubStatusName = githubDto.getStatus()[0].getStatus().getName();
        switch (githubStatusName.toUpperCase()) {
            case "OPEN":
            case "BACKLOG":
            case "NO STATUS":
                return TicketStatus.OPEN;
            case "IN PROGRESS": 
                return TicketStatus.IN_PROGRESS;
            case "IN REVIEW":
            case "IN TEST":
                return TicketStatus.IN_REVIEW;
            case "RESOLVED":
                return TicketStatus.RESOLVED;
            case "CLOSED":
            case "DONE":
                return TicketStatus.CLOSED;
            case "ON HOLD":
                return TicketStatus.ON_HOLD;
            default:
                System.err.println("Warning: Unrecognized status '" + githubStatusName + "'. Defaulting to OPEN.");
                return TicketStatus.OPEN; 
        }
    }
}