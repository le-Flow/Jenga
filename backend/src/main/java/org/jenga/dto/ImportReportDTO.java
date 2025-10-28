package org.jenga.dto;

import java.util.List;
import lombok.Data;

@Data 
public class ImportReportDTO {
    
    private int successfulImportCount;
    
    private List<String> failedImports;

    public ImportReportDTO(int successfulCount, List<String> failedImports) {
        this.successfulImportCount = successfulCount;
        this.failedImports = failedImports;
    }
}