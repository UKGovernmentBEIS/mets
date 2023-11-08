package uk.gov.pmrv.api.migration.emp.corsia.fummethods;

import org.apache.commons.lang3.ObjectUtils;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;
import uk.gov.pmrv.api.migration.emp.common.fummethods.EmpFuelUseMonitoringMethods;

public class EmpFuelUseMonitoringMethodsCorsiaMigrationMapper {

	public static EmpFuelUseMonitoringMethods toEmpFuelUseMonitoringMethods(EtsFuelUseMonitoringMethodsCorsia etsFuelUseMonitoringMethods) {

        return EmpFuelUseMonitoringMethods.builder()
        		.blockOnBlockOffMethodProcedures(buildBlockOnBlockOffMethodProcedures(etsFuelUseMonitoringMethods))
        		.methodAProcedures(buildMethodAProcedures(etsFuelUseMonitoringMethods))
        		.methodBProcedures(buildMethodBProcedures(etsFuelUseMonitoringMethods))
        		.fuelUpliftMethodProcedures(buildFuelUpliftMethodProcedures(etsFuelUseMonitoringMethods))
                .build();
    }

	private static EmpFuelUpliftMethodProcedures buildFuelUpliftMethodProcedures(
			EtsFuelUseMonitoringMethodsCorsia etsFuelUseMonitoringMethods) {
		return EmpFuelUpliftMethodProcedures
				.builder()
				.zeroFuelUplift(etsFuelUseMonitoringMethods.getZeroFuelUpliftDescription())
				.fuelUpliftSupplierRecordType(null)
				.blockHoursPerFlight(createProcedureForm(
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureTitle(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureReference(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureDescription(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedurePost(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureLocation(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureSystem()))
				.fuelDensity(createProcedureForm(
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureTitle(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureReference(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureDescription(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedurePost(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureLocation(),
						etsFuelUseMonitoringMethods.getFuelDensityFuelUpliftProcedureSystem()))
				.build();
	}

	private static EmpMethodBProcedures buildMethodBProcedures(
			EtsFuelUseMonitoringMethodsCorsia etsFuelUseMonitoringMethods) {
		return EmpMethodBProcedures
				.builder()
				.fuelConsumptionPerFlight(createProcedureForm(
						etsFuelUseMonitoringMethods.getMethodBProcedureTitle(),
						etsFuelUseMonitoringMethods.getMethodBProcedureReference(),
						etsFuelUseMonitoringMethods.getMethodBProcedureDescription(),
						etsFuelUseMonitoringMethods.getMethodBProcedurePost(),
						etsFuelUseMonitoringMethods.getMethodBProcedureLocation(),
						etsFuelUseMonitoringMethods.getMethodBProcedureSystem()))
				.fuelDensity(createProcedureForm(
						etsFuelUseMonitoringMethods.getFuelDensityMethodBProcedureTitle(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodBProcedureReference(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodBProcedureDescription(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodBProcedurePost(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodBProcedureLocation(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodBProcedureSystem()))
				.build();
	}

	private static EmpMethodAProcedures buildMethodAProcedures(
			EtsFuelUseMonitoringMethodsCorsia etsFuelUseMonitoringMethods) {
		return EmpMethodAProcedures
				.builder()
				.fuelConsumptionPerFlight(createProcedureForm(
						etsFuelUseMonitoringMethods.getMethodAProcedureTitle(),
						etsFuelUseMonitoringMethods.getMethodAProcedureReference(),
						etsFuelUseMonitoringMethods.getMethodAProcedureDescription(),
						etsFuelUseMonitoringMethods.getMethodAProcedurePost(),
						etsFuelUseMonitoringMethods.getMethodAProcedureLocation(),
						etsFuelUseMonitoringMethods.getMethodAProcedureSystem()))
				.fuelDensity(createProcedureForm(
						etsFuelUseMonitoringMethods.getFuelDensityMethodAProcedureTitle(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodAProcedureReference(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodAProcedureDescription(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodAProcedurePost(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodAProcedureLocation(),
						etsFuelUseMonitoringMethods.getFuelDensityMethodAProcedureSystem()))
				.build();
	}

	private static EmpBlockOnBlockOffMethodProcedures buildBlockOnBlockOffMethodProcedures(
			EtsFuelUseMonitoringMethodsCorsia etsFuelUseMonitoringMethods) {
		return EmpBlockOnBlockOffMethodProcedures
				.builder()
				.fuelConsumptionPerFlight(createProcedureForm(
						etsFuelUseMonitoringMethods.getBlockOffBlockOnProcedureTitle(),
						etsFuelUseMonitoringMethods.getBlockOffBlockOnProcedureReference(),
						etsFuelUseMonitoringMethods.getBlockOffBlockOnProcedureDescription(),
						etsFuelUseMonitoringMethods.getBlockOffBlockOnProcedurePost(),
						etsFuelUseMonitoringMethods.getBlockOffBlockOnProcedureLocation(),
						etsFuelUseMonitoringMethods.getBlockOffBlockOnProcedureSystem()))
				.build();
	}
	
	private static EmpProcedureForm createProcedureForm(String name, String reference, String description, String departmentRole,
            String recordsLocation, String itSystem) {

		if(ObjectUtils.isEmpty(name) && ObjectUtils.isEmpty(reference) && ObjectUtils.isEmpty(description) &&
				ObjectUtils.isEmpty(departmentRole) && ObjectUtils.isEmpty(recordsLocation) && ObjectUtils.isEmpty(itSystem)) {
		return null;
		}
		
		return EmpProcedureForm.builder()
				.procedureDocumentName(name)
				.procedureReference(reference)
				.procedureDescription(description)
				.responsibleDepartmentOrRole(departmentRole)
				.locationOfRecords(recordsLocation)
				.itSystemUsed(itSystem)
				.build();
		}
}
