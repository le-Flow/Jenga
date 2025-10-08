package org.jenga.db;

import org.jenga.model.Ticket;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {
    public List<Ticket> findByProjectId(Long projectId) {
        return find("project.id", projectId).list();
    }
    public Ticket findByIdAndProjectId(Long ticketId, Long projectId) {
        return find("id = ?1 and project.id = ?2", ticketId, projectId).firstResult();
    }
}
