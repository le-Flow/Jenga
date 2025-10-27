package org.jenga.dto;

import lombok.Data;

@Data
public class CreateProjectDTO {
    private String identifier;
    private String name;
    private String description;
}
