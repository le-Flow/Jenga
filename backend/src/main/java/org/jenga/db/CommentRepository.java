package org.jenga.db;

import java.util.List;

import org.jenga.model.Comment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommentRepository implements PanacheRepository<Comment> {

    public List<Comment> findByTicketId(Long ticketId) {
        return list("ticket.id", ticketId);
    }

    public Comment findByIdAndTicketId(Long commentId, Long ticketId) {
        return find("ticket.id = ?1 and id = ?2", ticketId, commentId).firstResult();
    }
    
    public void deleteByIdAndTicketId(Long commentId, Long ticketId) {
        delete("ticket.id = ?1 and id = ?2", ticketId, commentId);
    }
}
