package uk.gov.pmrv.api.migration.emp.corsia.monitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsMonitoringApproachCorsia {

	private String fldEmitterID;
    private String fldEmitterDisplayID;
    private int empVersion;
    private String corsiaMonitoringMethod;
    private String corsiaCertType;
    private String icaoCertUsedDesc;
    private String evidenceStoredFileName;
    private String evidenceUploadedFileName;
}
