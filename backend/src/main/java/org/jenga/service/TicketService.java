package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.model.Ticket;
import org.jenga.model.Project;
import org.jenga.dto.TicketDTO;
import org.jenga.dto.CreateTicketDTO;
import org.jenga.mapper.TicketMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final TicketMapper ticketMapper;

    @Inject
    public TicketService(TicketRepository ticketRepository, ProjectRepository projectRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    public void create(Long projectId, CreateTicketDTO createTicketDTO) {
        Project project = projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        if (createTicketDTO.getTitle() == null || createTicketDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("Ticket title cannot be null or empty");
        }

        Ticket ticket = ticketMapper.createTicketDTOToTicket(createTicketDTO);

        ticket.setProject(project);
        
        //ticket.setReporter(getCurrentUser()); TODO

        ticketRepository.persist(ticket);
    }

    public List<TicketDTO> findAll(Long projectId) {
        projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        return ticketRepository.findByProjectId(projectId).stream()
                .map(ticketMapper::ticketToTicketDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO findById(Long projectId, Long ticketId) {
        projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        return ticketMapper.ticketToTicketDTO(ticket);
    }

    @Transactional
    public void update(Long projectId, Long ticketId, TicketDTO ticketDTO) {
        projectRepository.findByIdOptional(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        Ticket existing = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (existing == null) {
            throw new NotFoundException("Ticket not found");
        }

        existing.setTitle(ticketDTO.getTitle());
        existing.setDescription(ticketDTO.getDescription());

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
