package org.jenga.dto;

import java.util.List;

import org.jenga.model.TicketPriority;
import org.jenga.model.TicketSize;
import org.jenga.model.TicketStatus;
import lombok.Data;

@Data
public class CreateTicketDTO {
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketSize size;
    private TicketStatus status;
    private String projectName;
    private String assignee;
    private List<String> labels;
}