package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.db.UserRepository;
import org.jenga.model.Ticket;
import org.jenga.model.Project;
import org.jenga.model.User;
import org.jenga.dto.TicketDTO;
import org.jenga.dto.CreateTicketDTO;
import org.jenga.mapper.TicketMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    @Inject
    public TicketService(TicketRepository ticketRepository, ProjectRepository projectRepository,  UserRepository userRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    public void create(String projectId, CreateTicketDTO createTicketDTO) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectId);
        }

        if (createTicketDTO.getTitle() == null || createTicketDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("Ticket title cannot be null or empty");
        }

        Ticket ticket = ticketMapper.createTicketDTOToTicket(createTicketDTO);
        ticket.setProject(project);

        ticket.setTicketNumber(ticketRepository.findMaxTicketNumberByProject(project) + 1);

        //ticket.setReporter(getCurrentUser()); // TODO: Add reporter

        if (createTicketDTO.getAssignee() != null && !createTicketDTO.getAssignee().isBlank()) {
            User user = userRepository.findByUsername(createTicketDTO.getAssignee());
            if (user != null) {
                ticket.setAssignee(user);
            } else {
                throw new BadRequestException("User not found with username: " + createTicketDTO.getAssignee());
            }
        }

        ticketRepository.persist(ticket);
    }

    public List<TicketDTO> findAll(String projectId) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectId);
        }

        return ticketRepository.findByProjectName(project.getName()).stream()
                .map(ticketMapper::ticketToTicketDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO findById(String projectId, Long ticketId) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectId);
        }

        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, project.getId());
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        return ticketMapper.ticketToTicketDTO(ticket);
    }

    public TicketDTO findByTicketNumber(String projectId, Long ticketNumber) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectId);
        }

        Ticket ticket = ticketRepository.findByTicketNumberAndProjectName(ticketNumber, project.getName());
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        return ticketMapper.ticketToTicketDTO(ticket);
    }

    @Transactional
    public void update(String projectId, Long ticketId, TicketDTO ticketDTO) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectId);
        }

        Ticket existing = ticketRepository.findByIdAndProjectId(ticketId, project.getId());
        if (existing == null) {
            throw new NotFoundException("Ticket not found");
        }

        existing.setTitle(ticketDTO.getTitle());
        existing.setDescription(ticketDTO.getDescription());

        ticketRepository.persist(existing);
    }

    @Transactional
    public void delete(String projectId, Long ticketId) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectId);
        }

        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, project.getId());
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        ticketRepository.delete(ticket);
    }

    @Transactional
    public void assignTicket(String projectId, Long ticketId, String username) {
        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new BadRequestException("Ticket not found");
        }
        User assignee = userRepository.findByUsername(username);
        if (assignee == null) {
            throw new BadRequestException("User not found");
        }

        ticket.setAssignee(assignee);
        ticketRepository.persist(ticket);
    }

    @Transactional
    public void unassignTicket(String projectId, Long ticketId) {
        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new BadRequestException("Ticket not found");
        }
        ticket.setAssignee(null);
        ticketRepository.persist(ticket);
    }
}