package org.jenga.db;

import org.jenga.model.Ticket;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {
    public List<Ticket> findByProjectName(String projectName) {
        return find("project.name", projectName).list();
    }

    public Ticket findByIdAndProjectName(Long ticketId, String projectName) {
        return find("id = ?1 and project.name = ?2", ticketId, projectName).firstResult();
    }
}
