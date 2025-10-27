package org.jenga.dto;

import lombok.Data;

@Data
public class AcceptanceCriteriaRequest {
    private String description;
    private boolean completed;
}
