package uk.gov.pmrv.api.migration.emp.corsia;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionsMonitoringPlanMigrationCorsiaContainer {

	private static final int DEFAULT_CONSOLIDATION_NUMBER_VERSION = 1;

    private EmissionsMonitoringPlanCorsiaContainer empContainer;

    @Builder.Default
    private List<FileAttachment> fileAttachments = new ArrayList<>();

    @Builder.Default
    private int consolidationNumber = DEFAULT_CONSOLIDATION_NUMBER_VERSION;
}
