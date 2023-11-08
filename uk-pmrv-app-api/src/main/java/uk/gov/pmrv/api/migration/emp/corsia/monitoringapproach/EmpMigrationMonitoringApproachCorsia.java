package uk.gov.pmrv.api.migration.emp.corsia.monitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproachCorsia;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpMigrationMonitoringApproachCorsia implements EmpCorsiaSection {

	private EmpEmissionsMonitoringApproachCorsia monitoringApproach;

    private EtsFileAttachment etsFileAttachment;
}
