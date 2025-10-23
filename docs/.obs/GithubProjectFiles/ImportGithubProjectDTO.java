package org.jenga.dto;

import lombok.Data;

@Data
public class ImportGithubProjectDTO {
    private String owner;
    private String repository;
}
