package org.jenga.db;

import org.jenga.model.Ticket;
import org.jenga.model.Project;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {
    public List<Ticket> findByProjectName(String projectName) {
        return find("project.name", projectName).list();
    }

    public Ticket findByIdAndProjectId(Long ticketId, String projectId) {
        return find("id = ?1 and project.id = ?2", ticketId, projectId).firstResult();
    }

    public Ticket findByTicketNumberAndProjectName(Long ticketNumber, String projectName) {
        return find("ticketNumber = ?1 and project.name = ?2", ticketNumber, projectName).firstResult();
    }

    public Long findMaxTicketNumberByProject(Project project) {
        Ticket ticket = find("project = ?1 and ticketNumber is not null", 
                            Sort.by("ticketNumber", Sort.Direction.Descending), project)
                            .firstResult();
        return ticket != null && ticket.getTicketNumber() != null ? ticket.getTicketNumber() : 0L;
    }
}
