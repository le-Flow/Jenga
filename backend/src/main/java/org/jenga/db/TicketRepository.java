package org.jenga.db;

import org.jenga.model.Ticket;
import org.jenga.model.Project;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {
    public List<Ticket> findByProjectId(String projectId) {
        return find("project.id", projectId).list();
    }

    public Ticket findByIdAndProjectId(Long ticketId, String projectId) {
        return find("id = ?1 and project.id = ?2", ticketId, projectId).firstResult();
    }

    public Ticket findByTicketNumberAndProjectId(Long ticketNumber, String projectId) {
        return find("ticketNumber = ?1 and project.id= ?2", ticketNumber, projectId).firstResult();
    }

    public Long findMaxTicketNumberByProject(Project project) {
        Ticket ticket = find("project = ?1 and ticketNumber is not null", 
                            Sort.by("ticketNumber", Sort.Direction.Descending), project)
                            .firstResult();
        return ticket != null && ticket.getTicketNumber() != null ? ticket.getTicketNumber() : 0L;
    }

    public boolean existsByGithubIssueId(Long githubIssueId) {
        return count("githubIssueId", githubIssueId) > 0;
    }
}
