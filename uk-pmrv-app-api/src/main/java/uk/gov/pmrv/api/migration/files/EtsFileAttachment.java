package uk.gov.pmrv.api.migration.files;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsFileAttachment {
    
    private String etsAccountId;
    private String uploadedFileName;
    private String storedFileName;
    private UUID uuid;
}
