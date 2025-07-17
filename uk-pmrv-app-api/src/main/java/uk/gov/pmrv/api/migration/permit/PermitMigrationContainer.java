package uk.gov.pmrv.api.migration.permit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.permit.domain.PermitContainer;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitMigrationContainer {

    private static final int DEFAULT_CONSOLIDATION_NUMBER_VERSION = 1;
    
    private PermitContainer permitContainer;
    
    @Builder.Default
    private List<FileAttachment> fileAttachments = new ArrayList<>();

    @Builder.Default
    private int consolidationNumber = DEFAULT_CONSOLIDATION_NUMBER_VERSION;
    
}
