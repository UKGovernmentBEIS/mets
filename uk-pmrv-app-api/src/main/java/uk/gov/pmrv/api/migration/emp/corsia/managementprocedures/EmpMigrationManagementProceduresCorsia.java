package uk.gov.pmrv.api.migration.emp.corsia.managementprocedures;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpManagementProceduresCorsia;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpMigrationManagementProceduresCorsia implements EmpCorsiaSection {
	
	private EmpManagementProceduresCorsia managementProcedures;

    private EtsFileAttachment dataFlowFileAttachment;
}
