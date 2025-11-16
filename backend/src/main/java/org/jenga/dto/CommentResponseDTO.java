package org.jenga.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentResponseDTO {
    private Long id;
    private String author;
    private String comment;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
}
