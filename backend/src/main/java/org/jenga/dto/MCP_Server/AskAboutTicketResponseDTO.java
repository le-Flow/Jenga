package org.jenga.dto.MCP_Server;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Adds the default constructor
@AllArgsConstructor // Adds the constructor with (String message, String ticketId)
public class AskAboutTicketResponseDTO {
    private String message;
    private String ticketId;
}
