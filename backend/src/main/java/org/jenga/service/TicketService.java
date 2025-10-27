package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.db.TicketRepository;
import org.jenga.db.UserRepository;
import org.jenga.db.CommentRepository;
import org.jenga.db.LabelRepository;
import org.jenga.db.AcceptanceCriteriaRepository;
import org.jenga.model.Ticket;
import org.jenga.model.Project;
import org.jenga.model.User;
import org.jenga.model.Comment;
import org.jenga.model.Label;
import org.jenga.model.AcceptanceCriteria;
import org.jenga.dto.TicketDTO;
import org.jenga.dto.CreateTicketDTO;
import org.jenga.dto.CommentRequestDTO;
import org.jenga.dto.CommentResponseDTO;
import org.jenga.dto.AcceptanceCriteriaRequest;
import org.jenga.dto.AcceptanceCriteriaResponse;
import org.jenga.mapper.TicketMapper;
import org.jenga.mapper.CommentMapper;
import org.jenga.mapper.AcceptanceCriteriaMapper;

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
    private final CommentRepository commentRepository;
    private final LabelRepository labelRepository;
    private final AcceptanceCriteriaRepository acceptanceCriteriaRepository;
    private final TicketMapper ticketMapper;
    private final CommentMapper commentMapper;
    private final AcceptanceCriteriaMapper acceptanceCriteriaMapper;
    private final AuthenticationService authenticationService;

    @Inject
    public TicketService(
        TicketRepository ticketRepository,
        ProjectRepository projectRepository,
        UserRepository userRepository,
        CommentRepository commentRepository,
        LabelRepository labelRepository,
        AcceptanceCriteriaRepository acceptanceCriteriaRepository,
        TicketMapper ticketMapper,
        CommentMapper commentMapper,
        AcceptanceCriteriaMapper acceptanceCriteriaMapper,
        AuthenticationService authenticationService
    ) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.labelRepository = labelRepository;
        this.acceptanceCriteriaRepository = acceptanceCriteriaRepository;
        this.ticketMapper = ticketMapper;
        this.commentMapper = commentMapper;
        this.acceptanceCriteriaMapper = acceptanceCriteriaMapper;
        this.authenticationService = authenticationService;
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

        User currentUser = authenticationService.getCurrentUser();
        ticket.setReporter(currentUser);

        if (createTicketDTO.getAssignee() != null && !createTicketDTO.getAssignee().isBlank()) {
            User user = userRepository.findByUsername(createTicketDTO.getAssignee());
            if (user != null) {
                ticket.setAssignee(user);
            } else {
                throw new BadRequestException("User not found with username: " + createTicketDTO.getAssignee());
            }
        }

        if (createTicketDTO.getLabels() != null && !createTicketDTO.getLabels().isEmpty()) {
            List<Label> labels = labelRepository.findByProjectIdAndNames(projectId, createTicketDTO.getLabels());
            if (labels.size() != createTicketDTO.getLabels().size()) {
                throw new BadRequestException("Label does not exist");
            }
            ticket.setLabels(labels);
        }

        ticketRepository.persist(ticket);
    }

    public List<TicketDTO> findAll(String projectId) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectId);
        }

        return ticketRepository.findByProjectId(project.getId()).stream()
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

        Ticket ticket = ticketRepository.findByTicketNumberAndProjectId(ticketNumber, project.getId());
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
    public TicketDTO duplicateTicket(String projectId, Long ticketId) {
        Ticket originalTicket = ticketRepository.findByIdAndProjectId(ticketId, projectId);

        if (originalTicket == null) {
            throw new NotFoundException("Ticket not found");
        }

        Project project = projectRepository.findById(projectId);

        Ticket duplicatedTicket = new Ticket();
        duplicatedTicket.setTicketNumber(ticketRepository.findMaxTicketNumberByProject(project) + 1);
        duplicatedTicket.setTitle(originalTicket.getTitle());
        duplicatedTicket.setDescription(originalTicket.getDescription());
        duplicatedTicket.setProject(project);
        duplicatedTicket.setPriority(originalTicket.getPriority());
        duplicatedTicket.setSize(originalTicket.getSize());
        duplicatedTicket.setStatus(originalTicket.getStatus());
        duplicatedTicket.setReporter(authenticationService.getCurrentUser());
        duplicatedTicket.setAssignee(originalTicket.getAssignee());

        List<Label> duplicatedLabels = originalTicket.getLabels().stream()
                .map(label -> {
                    Label newLabel = new Label();
                    newLabel.setId(label.getId());
                    newLabel.setName(label.getName());
                    return newLabel;
                })
                .collect(Collectors.toList());

        duplicatedTicket.setLabels(duplicatedLabels);

        List<AcceptanceCriteria> duplicatedCriteria = originalTicket.getAcceptanceCriteria().stream()
                .map(criterion -> {
                    AcceptanceCriteria newCriterion = new AcceptanceCriteria();
                    newCriterion.setDescription(criterion.getDescription());
                    newCriterion.setCompleted(criterion.isCompleted());
                    newCriterion.setTicket(duplicatedTicket);
                    return newCriterion;
                })
                .collect(Collectors.toList());

        duplicatedTicket.setAcceptanceCriteria(duplicatedCriteria);

        ticketRepository.persist(duplicatedTicket);

        return ticketMapper.ticketToTicketDTO(duplicatedTicket);
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

    @Transactional
    public void createComment(String projectId, Long ticketId, CommentRequestDTO commentDTO) {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found");
        }

        Comment comment = commentMapper.commentRequestDTOToComment(commentDTO);

        User currentUser = authenticationService.getCurrentUser();
        comment.setAuthor(currentUser);

        comment.setTicket(ticket);

        commentRepository.persist(comment);
    }

    public List<CommentResponseDTO> getAllComments(String projectId, Long ticketId) {
        List<Comment> comments = commentRepository.findByTicketId(ticketId);

        return comments.stream()
                .map(commentMapper::commentToCommentResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(String projectId, Long ticketId, Long commentId) {
        Comment comment = commentRepository.findByIdAndTicketId(commentId, ticketId);
        if (comment == null) {
            throw new NotFoundException("Comment not found");
        }

        commentRepository.deleteByIdAndTicketId(commentId, ticketId);
    }

    @Transactional
    public AcceptanceCriteriaResponse addAcceptanceCriteria(String projectId, Long ticketId, AcceptanceCriteriaRequest request) {
        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        AcceptanceCriteria criteria = acceptanceCriteriaMapper.toEntity(request);
        criteria.setTicket(ticket);

        acceptanceCriteriaRepository.persist(criteria);
        return acceptanceCriteriaMapper.toResponse(criteria);
    }

    public List<AcceptanceCriteriaResponse> getAllAcceptanceCriteria(String projectId, Long ticketId) {
        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        List<AcceptanceCriteria> criteriaList = acceptanceCriteriaRepository.findByTicketId(ticketId);
        return criteriaList.stream()
                .map(acceptanceCriteriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAcceptanceCriteria(String projectId, Long ticketId, Long criteriaId, AcceptanceCriteriaRequest request) {
        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        AcceptanceCriteria criteria = acceptanceCriteriaRepository.findByIdAndTicketId(criteriaId, ticketId);
        if (criteria == null) {
            throw new NotFoundException("Acceptance criteria not found");
        }

        criteria.setDescription(request.getDescription());
        criteria.setCompleted(request.isCompleted());

        acceptanceCriteriaRepository.persist(criteria);
    }

    @Transactional
    public void deleteAcceptanceCriteria(String projectId, Long ticketId, Long criteriaId) {
        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        AcceptanceCriteria criteria = acceptanceCriteriaRepository.findByIdAndTicketId(criteriaId, ticketId);
        if (criteria == null) {
            throw new NotFoundException("Acceptance criteria not found");
        }

        acceptanceCriteriaRepository.deleteByIdAndTicketId(criteriaId, ticketId);
    }

    @Transactional
    public void addRelatedTicket(String projectId, Long ticketId, Long relatedTicketId) {
        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        Ticket relatedTicket = ticketRepository.findByIdAndProjectId(relatedTicketId, projectId);
        if (relatedTicket == null) {
            throw new NotFoundException("Related ticket not found");
        }

        ticket.getRelatedTickets().add(relatedTicket);

        relatedTicket.getRelatedTickets().add(ticket);

        // Persist changes to both tickets, so both are linking each other
        ticketRepository.persist(ticket);
        ticketRepository.persist(relatedTicket);
    }

    @Transactional
    public void removeRelatedTicket(String projectId, Long ticketId, Long relatedTicketId) {
        Ticket ticket = ticketRepository.findByIdAndProjectId(ticketId, projectId);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }

        Ticket relatedTicket = ticketRepository.findByIdAndProjectId(relatedTicketId, projectId);
        if (relatedTicket == null) {
            throw new NotFoundException("Related ticket not found");
        }

        ticket.getRelatedTickets().remove(relatedTicket);

        relatedTicket.getRelatedTickets().remove(ticket);

        // Persist changes to both tickets, so both are linking each other
        ticketRepository.persist(ticket);
        ticketRepository.persist(relatedTicket);
    }
}
