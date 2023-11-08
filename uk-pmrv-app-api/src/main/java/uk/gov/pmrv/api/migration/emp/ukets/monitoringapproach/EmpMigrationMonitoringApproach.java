package uk.gov.pmrv.api.migration.emp.ukets.monitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproach;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpMigrationMonitoringApproach implements EmpUkEtsSection {

    private EmpEmissionsMonitoringApproach monitoringApproach;

    private EtsFileAttachment etsFileAttachment;

}
