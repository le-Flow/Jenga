package org.jenga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptanceCriteriaRequestDTO {
    @NotBlank
    private String description;

    @NotNull
    private boolean completed;
}
