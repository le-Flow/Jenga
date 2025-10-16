package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.model.Ticket;
import org.jenga.model.Project;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;

    @Inject
    public TicketService(TicketRepository ticketRepository, ProjectRepository projectRepository) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void create(Long projectId, Ticket ticket) {
        Project project = projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        // Validate ticket title using getter
        if (ticket.getTitle() == null || ticket.getTitle().isBlank()) {
            throw new IllegalArgumentException("Ticket title cannot be null or empty");
        }

        // Reset ID to ensure it's treated as a new entity
        ticket.setId(null);

        // Use setter for associating the project
        ticket.setProject(project);

        ticketRepository.persist(ticket);
    }

    public List<Ticket> findAll(Long projectId) {
        projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        return ticketRepository.findByProjectId(projectId);
    }

    public Ticket findById(Long projectId, Long ticketId) {
        projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }
        return ticket;
    }

    @Transactional
    public void update(Long projectId, Long ticketId, Ticket ticket) {
        projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        Ticket existing = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (existing == null) {
            throw new NotFoundException("Ticket not found");
        }

        existing.setTitle(ticket.getTitle());
        existing.setDescription(ticket.getDescription());
        ticketRepository.persist(existing);
    }

    @Transactional
    public void delete(Long projectId, Long ticketId) {
        projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        ticketRepository.delete(ticket);
    }
}
