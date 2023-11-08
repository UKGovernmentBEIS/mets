package uk.gov.pmrv.api.migration.emp.corsia.managementprocedures;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpManagementProceduresCorsia {
	
	private String fldEmitterID;
    private String fldEmitterDisplayID;
    private List<String>  jobDetails;
    private String procedureDescription;
    private String recordKeepingDescription;
    private String riskDescription;
    private String revisionsDescription;
    private String storedFileName;
    private String uploadedFileName;
}
