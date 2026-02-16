package org.jenga.service;

import org.jenga.dto.TicketRequestDTO;
import org.jenga.dto.TicketResponseDTO;
import org.jenga.dto.ProjectRequestDTO;
import org.jenga.dto.RegisterRequestDTO;
import org.jenga.model.TicketPriority;
import org.jenga.model.TicketSize;
import org.jenga.model.TicketStatus;
import org.jenga.model.User;
import org.jenga.dto.CommentRequestDTO;
import org.jenga.dto.CommentResponseDTO;
import org.jenga.dto.AcceptanceCriteriaRequestDTO;
import org.jenga.dto.AcceptanceCriteriaResponseDTO;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.InjectMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import org.mockito.Mockito;

@QuarkusTest
@TestSecurity(user = "testuser1")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketServiceTest {
    @Inject
    TicketService service;

    @Inject
    ProjectService projectService;

    @InjectMock
    AuthenticationService authenticationService;
    
    @Inject
    jakarta.persistence.EntityManager entityManager;

    private static final String PROJECT_IDENTIFIER = "testproject";
    private static final String PROJECT_NAME = "Test Project";
    private static final String PROJECT_DESCRIPTION = "Sample Test Project";

    private static final String USER_NAME = "testuser1";

    @BeforeEach
    void setupMock() {
        var mockUser = new User(); // assuming your User entity/class
        mockUser.setUsername(USER_NAME);
        mockUser.setEmail("test@test.com");

        Mockito.when(authenticationService.getCurrentUser())
               .thenReturn(mockUser);
    }

    @BeforeAll
    @Transactional
    void setup() {
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setIdentifier(PROJECT_IDENTIFIER);
        dto.setName(PROJECT_NAME);
        dto.setDescription(PROJECT_DESCRIPTION);
        
        projectService.create(dto);

        RegisterRequestDTO userDto = new RegisterRequestDTO();
        userDto.setEmail("test@test.com");
        userDto.setUsername(USER_NAME);
        userDto.setPassword("password");

        authenticationService.register(userDto);
    }

    @AfterAll
    @Transactional
    void tearDown() {
        projectService.delete(PROJECT_IDENTIFIER);

        entityManager.createQuery("DELETE FROM User u WHERE u.username = :username")
             .setParameter("username", USER_NAME)
             .executeUpdate();
    }

    @Test
    @TestTransaction
    void testCreateTicket() {
        TicketRequestDTO ticket = new TicketRequestDTO();
        ticket.setTitle("Test Ticket");
        ticket.setPriority(TicketPriority.CRITICAL);
        ticket.setSize(TicketSize.TINY);
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setReporter(USER_NAME);

        TicketResponseDTO response = service.create(PROJECT_IDENTIFIER, ticket);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getTicketNumber());
        assertEquals("Test Ticket", response.getTitle());
        assertEquals(TicketPriority.CRITICAL, response.getPriority());
        assertEquals(TicketSize.TINY, response.getSize());
        assertEquals(TicketStatus.RESOLVED, response.getStatus());
        assertEquals(USER_NAME, response.getReporter());

        Long ticketId = response.getId();
        
        String myComment = "My comment";
        CommentRequestDTO comment = new CommentRequestDTO();
        comment.setComment(myComment);

        CommentResponseDTO responseComment = service.createComment(ticketId, comment);
        assertNotNull(responseComment);
        assertNotNull(responseComment.getId());
        assertEquals(responseComment.getComment(), myComment);
        assertEquals(responseComment.getAuthor(), USER_NAME);

        String myDescription = "My description";
        AcceptanceCriteriaRequestDTO ac = new AcceptanceCriteriaRequestDTO();
        ac.setDescription(myDescription);
        ac.setCompleted(true);

        AcceptanceCriteriaResponseDTO acResponse =  service.addAcceptanceCriteria(ticketId, ac);
        assertNotNull(acResponse);
        assertNotNull(acResponse.getId());
        assertEquals(acResponse.getDescription(), myDescription);
        assertEquals(acResponse.isCompleted(), true);
    }

    @Test
    void testCreateCommentInvalidTicket() {
        Long invalidId = 123456L;

        CommentRequestDTO comment = new CommentRequestDTO();
        comment.setComment("My comment");

        assertThrows(NotFoundException.class, () -> service.createComment(invalidId, comment));
    }

    @Test
    void testCreateAcceptanceCriteriaInvalidTicket() {
        Long invalidId = 123456L;

        AcceptanceCriteriaRequestDTO ac = new AcceptanceCriteriaRequestDTO();
        ac.setDescription("My descrption");
        ac.setCompleted(true);

        assertThrows(NotFoundException.class, () -> service.addAcceptanceCriteria(invalidId, ac));
    }
}
