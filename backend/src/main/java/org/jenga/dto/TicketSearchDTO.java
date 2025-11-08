package org.jenga.dto;

import java.util.List;

import org.jenga.model.TicketPriority;
import org.jenga.model.TicketSize;
import org.jenga.model.TicketStatus;

import lombok.Data;

@Data
public class TicketSearchDTO{
    private String query;
    private String projectId;
    private List<TicketPriority> priority;
    private List<TicketSize> size;
    private List<TicketStatus> status;
    private List<String> reporter;
    private List<String> assignee;
}
