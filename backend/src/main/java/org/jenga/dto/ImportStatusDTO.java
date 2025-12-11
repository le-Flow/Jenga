package org.jenga.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportStatusDTO {

    private boolean success;
    private long importedCount;
    private String message;

    public ImportStatusDTO(boolean success, long importedCount) {
        this.success = success;
        this.importedCount = importedCount;
        this.message = "Successfully imported " + importedCount + " tickets.";
    }

    public ImportStatusDTO(boolean success, String message) {
        this.success = success;
        this.importedCount = 0;
        this.message = message;
    }
}