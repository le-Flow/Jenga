package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.model.Ticket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class TicketService {
    @Inject
    TicketRepository ticketRepository;

    @Inject
    ProjectRepository projectRepository;

    public void create(Long projectId, Ticket ticket) {
 
    }

    public List<Ticket> findAll(Long projectId) {
        return ticketRepository.findAll().list();
    }

    public Ticket findById(Long projectId, Long ticketId) {
        return ticketRepository.findByIdOptional(ticketId)
            .orElseThrow(() -> new NotFoundException("Ticket not found"));
    }

    public void update(Long projectId, Long ticketId, Ticket ticket) {

    }

    public void delete(Long projectId, Long ticketId) {
    }
}
