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
    public void create(String projectName, CreateTicketDTO createTicketDTO) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectName);
        }

        if (createTicketDTO.getTitle() == null || createTicketDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("Ticket title cannot be null or empty");
        }

        Ticket ticket = ticketMapper.createTicketDTOToTicket(createTicketDTO);
        ticket.setProject(project);

        ticket.setTicketNumber(ticketRepository.findMaxTicketNumberByProject(project) + 1);

        //ticket.setReporter(getCurrentUser()); // TODO: Add reporter

        ticketRepository.persist(ticket);
    }

    public List<TicketDTO> findAll(String projectName) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectName);
        }

        return ticketRepository.findByProjectName(project.getName()).stream()
                .map(ticketMapper::ticketToTicketDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO findById(String projectName, Long ticketId) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectName);
        }

        Ticket ticket = ticketRepository.findByIdAndProjectName(ticketId, project.getName());
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        return ticketMapper.ticketToTicketDTO(ticket);
    }

    @Transactional
    public void update(String projectName, Long ticketId, TicketDTO ticketDTO) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectName);
        }

        Ticket existing = ticketRepository.findByIdAndProjectName(ticketId, project.getName());
        if (existing == null) {
            throw new NotFoundException("Ticket not found");
        }

        existing.setTitle(ticketDTO.getTitle());
        existing.setDescription(ticketDTO.getDescription());

        ticketRepository.persist(existing);
    }

    @Transactional
    public void delete(String projectName, Long ticketId) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectName);
        }

        Ticket ticket = ticketRepository.findByIdAndProjectName(ticketId, project.getName());
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        ticketRepository.delete(ticket);
    }
}
