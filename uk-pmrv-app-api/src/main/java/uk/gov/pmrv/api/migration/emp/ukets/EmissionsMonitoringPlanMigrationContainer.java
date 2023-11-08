package uk.gov.pmrv.api.migration.emp.ukets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionsMonitoringPlanMigrationContainer {

    private static final int DEFAULT_CONSOLIDATION_NUMBER_VERSION = 1;

    private EmissionsMonitoringPlanUkEtsContainer empContainer;

    @Builder.Default
    private List<FileAttachment> fileAttachments = new ArrayList<>();

    @Builder.Default
    private int consolidationNumber = DEFAULT_CONSOLIDATION_NUMBER_VERSION;
}
