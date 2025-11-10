package org.jenga.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jenga.db.LabelRepository;
import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.db.UserRepository;
import org.jenga.model.Label;
import org.jenga.model.Project;
import org.jenga.model.Ticket;
import org.jenga.model.TicketPriority;
import org.jenga.model.TicketSize;
import org.jenga.model.TicketStatus;
import org.jenga.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CreateTicketTool {

    @Inject
    TicketRepository ticketRepository;
    @Inject
    ProjectRepository projectRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    LabelRepository labelRepository;

    @Tool("Creates a new software development ticket (e.g., bug, feature, task) in the specified project.")
    @Transactional 
    public String createTicket(
            @P("The title or summary of the ticket. This is mandatory.")
            String title,

            @P("A detailed description of the ticket, including steps to reproduce for bugs. This is mandatory.")
            String description,

            @P("The project ID (e.g., 'MCP', 'Frontend'). This is mandatory.")
            String projectId, 
            
            @P("The username of the person reporting the ticket. Can be null or 'unassigned'.") 
            String reporterUsername, 

            @P("The username of the person to assign the ticket to. Can be null or 'unassigned'.")
            String assigneeUsername, 

            @P("The priority of the ticket. Valid values are LOW, MEDIUM, HIGH, CRITICAL.")
            TicketPriority priority,

            @P("The estimated size or effort. Valid values are SMALL, MEDIUM, LARGE, EXTRA_LARGE.")
            TicketSize size,

            @P("A list of label names to add to the ticket, e.g., ['bug', 'ui', 'backend'].")
            List<String> labels
    ) {
        try {
            Project project = projectRepository.findById(projectId);
            if (project == null) {
                return "ERROR: Could not create ticket. Reason: Project not found with ID: " + projectId;
            }

            Ticket newTicket = new Ticket();
            newTicket.setTitle(title);
            newTicket.setDescription(description);
            newTicket.setProject(project);
            newTicket.setStatus(TicketStatus.OPEN); 

            if (reporterUsername != null && !reporterUsername.isEmpty() && !reporterUsername.equalsIgnoreCase("unassigned")) {
                User reporter = userRepository.findByUsername(reporterUsername);
                if (reporter != null) {
                    newTicket.setReporter(reporter);
                } else {
                    System.err.println("Warning: Reporter not found, ticket will have no reporter: " + reporterUsername);
                }
            }

            if (priority != null) {
                newTicket.setPriority(priority);
            }
            if (size != null) {
                newTicket.setSize(size);
            }

            if (assigneeUsername != null && !assigneeUsername.isEmpty() && !assigneeUsername.equalsIgnoreCase("unassigned")) {
                User assignee = userRepository.findByUsername(assigneeUsername);
                if (assignee != null) {
                    newTicket.setAssignee(assignee);
                } else {
                    System.err.println("Warning: Assignee not found, ticket will be unassigned: " + assigneeUsername);
                }
            }

            if (labels != null && !labels.isEmpty()) {
                Set<Label> labelsToAttach = new HashSet<>();
                for (String labelName : labels) {
                    Label label = labelRepository.find("project = ?1 and name = ?2", project, labelName).firstResult();

                    if (label == null) {
                        label = new Label();
                        label.setName(labelName);
                        label.setProject(project);
                        labelRepository.persist(label);
                    }
                    labelsToAttach.add(label);
                }
                newTicket.setLabels(new ArrayList<>(labelsToAttach));
            }

            newTicket.setTicketNumber(ticketRepository.findMaxTicketNumberByProject(project) + 1);

            ticketRepository.persist(newTicket);

            return "SUCCESS: Created new ticket " +
                   newTicket.getProject().getId() + "-" + newTicket.getTicketNumber() +
                   ": '" + newTicket.getTitle() + "'.";

        } catch (Exception e) {
            return "ERROR: An unexpected error occurred: " + e.getMessage();
        }
    }
}