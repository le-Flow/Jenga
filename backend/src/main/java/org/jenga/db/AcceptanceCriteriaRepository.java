package org.jenga.db;

import java.util.List;

import org.jenga.model.AcceptanceCriteria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AcceptanceCriteriaRepository implements PanacheRepository<AcceptanceCriteria> {
    public List<AcceptanceCriteria> findByTicketId(Long ticketId) {
        return list("ticket.id", ticketId);
    }

    public AcceptanceCriteria findByIdAndTicketId(Long criteriaId, Long ticketId) {
        return find("ticket.id = ?1 and id = ?2", ticketId, criteriaId).firstResult();
    }

    public void deleteByIdAndTicketId(Long criteriaId, Long ticketId) {
        delete("ticket.id = ?1 and id = ?2", ticketId, criteriaId);
    }
}
