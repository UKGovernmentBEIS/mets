package uk.gov.pmrv.api.migration.emp.ukets.emissionsreductionclaim;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsreductionclaim.EmpEmissionsReductionClaim;

public class EmpEmissionsReductionClaimMigrationMapper {

	public static EmpEmissionsReductionClaim toEmpEmissionsReductionClaim(
			EtsEmpEmissionsReductionClaim etsEmpEmissionsReductionClaim) {
		
		return EmpEmissionsReductionClaim.builder()
				.exist(true)
                .safMonitoringSystemsAndProcesses(EmpProcedureForm.builder()
                        .procedureDocumentName(etsEmpEmissionsReductionClaim.getPurchaseDeliveryTitle())
                        .procedureReference(etsEmpEmissionsReductionClaim.getPurchaseDeliveryReference())
                        .procedureDescription(etsEmpEmissionsReductionClaim.getPurchaseDeliveryDescription())
                        .responsibleDepartmentOrRole(etsEmpEmissionsReductionClaim.getPurchaseDeliveryPost())
                        .locationOfRecords(etsEmpEmissionsReductionClaim.getPurchaseDeliveryLocation())
                        .itSystemUsed(etsEmpEmissionsReductionClaim.getPurchaseDeliverySystem())
                        .build())
                .rtfoSustainabilityCriteria(EmpProcedureForm.builder()
                        .procedureDocumentName(etsEmpEmissionsReductionClaim.getSustainabilityTitle())
                        .procedureReference(etsEmpEmissionsReductionClaim.getSustainabilityReference())
                        .procedureDescription(etsEmpEmissionsReductionClaim.getSustainabilityDescription())
                        .responsibleDepartmentOrRole(etsEmpEmissionsReductionClaim.getSustainabilityPost())
                        .locationOfRecords(etsEmpEmissionsReductionClaim.getSustainabilityLocation())
                        .itSystemUsed(etsEmpEmissionsReductionClaim.getSustainabilitySystem())
                        .build())
                .safDuplicationPrevention(EmpProcedureForm.builder()
                        .procedureDocumentName(etsEmpEmissionsReductionClaim.getAvoidanceTitle())
                        .procedureReference(etsEmpEmissionsReductionClaim.getAvoidanceReference())
                        .procedureDescription(etsEmpEmissionsReductionClaim.getAvoidanceDescription())
                        .responsibleDepartmentOrRole(etsEmpEmissionsReductionClaim.getAvoidancePost())
                        .locationOfRecords(etsEmpEmissionsReductionClaim.getAvoidanceLocation())
                        .itSystemUsed(etsEmpEmissionsReductionClaim.getAvoidanceSystem())
                        .build())
                .build();	
	}
}
