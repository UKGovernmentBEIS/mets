package uk.gov.pmrv.api.migration.emp.ukets.monitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpMonitoringApproach {

    private String etsAccountId;
    private boolean isSimplifiedProcedure;
    private String monitoringApproach;
    private String simplifiedReportingEligibility;
    private String storedFileName;
    private String uploadedFileName;
}
