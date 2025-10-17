package org.jenga.dto;

import java.time.LocalDateTime;
import org.jenga.model.TicketPriority;
import org.jenga.model.TicketSize;
import org.jenga.model.TicketStatus;

import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private TicketPriority priority;
    private TicketSize size;
    private TicketStatus status;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long reporterId;
    private Long assigneeId;
}
