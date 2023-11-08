package uk.gov.pmrv.api.migration.emp.ukets.managementprocedures;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpMigrationManagementProcedures implements EmpUkEtsSection {

    private EmpManagementProcedures managementProcedures;

    private EtsFileAttachment dataFlowFileAttachment;

    private EtsFileAttachment riskAssessmentFileAttachment;
}
