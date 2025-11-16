package org.jenga.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) 
public class GitHubIssueDTO {
    @JsonProperty("milestone")
    private GitHubMilestoneDTO epic;

    @JsonProperty("number")
    private int ticketNumber; 
    
    private GitHubAssigneeDTO[] assignees;
    private String title;
    private GitHubCommentDTO[] comments; // Comments = author, body, createdAt
    private String createdAt;
    private String updatedAt;

    @JsonProperty("projectItems")
    private GitHubProjectItemDTO[] status; // projectItems/status/name, Array cause of Datatype in Json
    private GitHubLabelDTO[] labels; // Labels = Name, Description, colour

    // Type = Label with name Test, Bug, Feature, etc.
    
    // Body = Specification + Acceptance Criteria + Relationships
    private String body;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubMilestoneDTO {
        private String title;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubAssigneeDTO {
        private String login; 
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubLabelDTO {
        private String name;
        private String description;
        private String color;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubCommentDTO {
        private String body;
        private String createdAt;
        private GitHubAssigneeDTO author; 
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubProjectItemDTO {
        private GitHubProjectStatusDTO status;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubProjectStatusDTO {
        private String name; 
    }
}