package org.jenga.dto;

import java.util.List;

import java.time.LocalDateTime;
import org.jenga.model.TicketPriority;
import org.jenga.model.TicketSize;
import org.jenga.model.TicketStatus;

import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private Long ticketNumber;
    private String title;
    private String description;
    private String projectName;
    private TicketPriority priority;
    private TicketSize size;
    private TicketStatus status;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private String reporterName;
    private String assigneeName;
    private List<String> labels;
    private List<AcceptanceCriteriaResponse> acceptanceCriteria;
    private List<Long> relatedTicketsIds;
}
