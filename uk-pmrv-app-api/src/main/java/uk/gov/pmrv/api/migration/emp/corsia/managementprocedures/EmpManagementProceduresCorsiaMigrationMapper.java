package uk.gov.pmrv.api.migration.emp.corsia.managementprocedures;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;

import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpDataManagement;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpManagementProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpMonitoringReportingRoleCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpMonitoringReportingRolesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpProcedureDescription;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

public class EmpManagementProceduresCorsiaMigrationMapper {
	
	public static EmpMigrationManagementProceduresCorsia toEmpManagementProcedures(EtsEmpManagementProceduresCorsia etsEmpManagementProcedures) {
		EtsFileAttachment dataFlowFileAttachment = createEtsFileAttachment(etsEmpManagementProcedures.getStoredFileName(),
				etsEmpManagementProcedures.getUploadedFileName(), etsEmpManagementProcedures.getFldEmitterID());
		
        return EmpMigrationManagementProceduresCorsia.builder()
        		.managementProcedures(EmpManagementProceduresCorsia.builder()
        				.monitoringReportingRoles(buildMonitoringReportingRoles(etsEmpManagementProcedures.getJobDetails()))
        	            .dataManagement(buildDataManagement(etsEmpManagementProcedures, dataFlowFileAttachment))
        	            .recordKeepingAndDocumentation(buildProcedureDescription(etsEmpManagementProcedures.getRecordKeepingDescription()))
        	            .riskExplanation(buildProcedureDescription(etsEmpManagementProcedures.getRiskDescription()))
        	            .empRevisions(buildProcedureDescription(etsEmpManagementProcedures.getRevisionsDescription()))
        	            .build())
        		.dataFlowFileAttachment(dataFlowFileAttachment)
	            .build();
    }

	private static EmpDataManagement buildDataManagement(EtsEmpManagementProceduresCorsia etsEmpManagementProcedures,
			EtsFileAttachment dataFlowFileAttachment) {
		return EmpDataManagement.builder()
				.description(etsEmpManagementProcedures.getProcedureDescription())
				.dataFlowDiagram(dataFlowFileAttachment != null ? dataFlowFileAttachment.getUuid() : null)
				.build();
	}

	private static EmpProcedureDescription buildProcedureDescription(String description) {
		return EmpProcedureDescription.builder()
				.description(description)
				.build();
	}
	
	private static EmpMonitoringReportingRolesCorsia buildMonitoringReportingRoles(List<String> jobDetails) {
        List<EmpMonitoringReportingRoleCorsia> monitoringReportingRoles =
            jobDetails.stream().map(EmpManagementProceduresCorsiaMigrationMapper::createEmpReportingRole).filter(Objects::nonNull).toList();

        return ObjectUtils.isNotEmpty(monitoringReportingRoles) ? 
        		EmpMonitoringReportingRolesCorsia.builder().monitoringReportingRoles(monitoringReportingRoles).build() : null;
    }

    private static EmpMonitoringReportingRoleCorsia createEmpReportingRole(String jobDetail) {
        final List<String> jobDetailsList = Arrays.stream(jobDetail.split("\\|col\\|"))
                .filter(s -> !s.isEmpty())
                .map(String::trim).toList();
        return jobDetailsList.size() > 1 ? EmpMonitoringReportingRoleCorsia.builder()
                .jobTitle(jobDetailsList.get(0))
                .mainDuties(jobDetailsList.get(1))
                .build() : null;
    }

	
	private static EtsFileAttachment createEtsFileAttachment(String storedFileName, String uploadedFileName, String etsAccountId) {

        return storedFileName != null && uploadedFileName != null &&
                MigrationConstants.ALLOWED_FILE_TYPES.contains(uploadedFileName.substring(uploadedFileName.lastIndexOf(".")).toLowerCase()) ?
                EtsFileAttachment.builder()
                        .etsAccountId(etsAccountId)
                        .uploadedFileName(uploadedFileName)
                        .storedFileName(storedFileName)
                        .uuid(UUID.randomUUID())
                        .build() : null;
    }
}
