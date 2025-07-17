package uk.gov.pmrv.api.migration.permit.digitizedMmp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DigitizedMmpMigrationError {

    private String accountId;
    private String permitId;
    private String fileUuid;
    private String fileName;
    private String worksheetName;
    private String errorReport;
    
}
