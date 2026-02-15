package org.jenga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequestDTO {
    @NotBlank
    private String identifier;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
