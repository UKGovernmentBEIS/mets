package uk.gov.pmrv.api.migration.emp.ukets.fummethods;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelUpliftSupplierRecordType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;
import uk.gov.pmrv.api.migration.emp.common.fummethods.EmpFuelUseMonitoringMethods;

public class EmpFuelUseMonitoringMethodsMigrationMapper {
	    
    public static EmpFuelUseMonitoringMethods toEmpFuelUseMonitoringMethods(EtsFuelUseMonitoringMethods etsFuelUseMonitoringMethods) {

        return EmpFuelUseMonitoringMethods.builder()
        		.blockOnBlockOffMethodProcedures(buildBlockOnBlockOffMethodProcedures(etsFuelUseMonitoringMethods))
        		.methodAProcedures(buildMethodAProcedures(etsFuelUseMonitoringMethods))
        		.methodBProcedures(buildMethodBProcedures(etsFuelUseMonitoringMethods))
        		.fuelUpliftMethodProcedures(buildFuelUpliftMethodProcedures(etsFuelUseMonitoringMethods))
                .build();
    }

	private static EmpFuelUpliftMethodProcedures buildFuelUpliftMethodProcedures(
			EtsFuelUseMonitoringMethods etsFuelUseMonitoringMethods) {
		return EmpFuelUpliftMethodProcedures
				.builder()
				.blockHoursPerFlight(createProcedureForm(
						etsFuelUseMonitoringMethods.getProcedureDetailsTitle(),
						etsFuelUseMonitoringMethods.getProcedureDetailsReference(),
						etsFuelUseMonitoringMethods.getProcedureDetailsDescription(),
						etsFuelUseMonitoringMethods.getProcedureDetailsPost(),
						etsFuelUseMonitoringMethods.getProcedureDetailsLocation(),
						etsFuelUseMonitoringMethods.getProcedureDetailsSystem()))
				.fuelDensity(createProcedureForm(
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureTitle(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureReference(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureDescription(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedurePost(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureLocation(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureSystem()))
				.fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
				.build();
	}

	private static EmpMethodBProcedures buildMethodBProcedures(
			EtsFuelUseMonitoringMethods etsFuelUseMonitoringMethods) {
		return EmpMethodBProcedures
				.builder()
				.fuelConsumptionPerFlight(createProcedureForm(
						etsFuelUseMonitoringMethods.getProcedureDetailsTitle(),
						etsFuelUseMonitoringMethods.getProcedureDetailsReference(),
						etsFuelUseMonitoringMethods.getProcedureDetailsDescription(),
						etsFuelUseMonitoringMethods.getProcedureDetailsPost(),
						etsFuelUseMonitoringMethods.getProcedureDetailsLocation(),
						etsFuelUseMonitoringMethods.getProcedureDetailsSystem()))
				.fuelDensity(createProcedureForm(
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureTitle(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureReference(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureDescription(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedurePost(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureLocation(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureSystem()))
				.build();
	}

	private static EmpMethodAProcedures buildMethodAProcedures(
			EtsFuelUseMonitoringMethods etsFuelUseMonitoringMethods) {
		return EmpMethodAProcedures
				.builder()
				.fuelConsumptionPerFlight(createProcedureForm(
						etsFuelUseMonitoringMethods.getProcedureDetailsTitle(),
						etsFuelUseMonitoringMethods.getProcedureDetailsReference(),
						etsFuelUseMonitoringMethods.getProcedureDetailsDescription(),
						etsFuelUseMonitoringMethods.getProcedureDetailsPost(),
						etsFuelUseMonitoringMethods.getProcedureDetailsLocation(),
						etsFuelUseMonitoringMethods.getProcedureDetailsSystem()))
				.fuelDensity(createProcedureForm(
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureTitle(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureReference(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureDescription(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedurePost(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureLocation(),
						etsFuelUseMonitoringMethods.getDensityMeasurementProcedureSystem()))
				.build();
	}

	private static EmpBlockOnBlockOffMethodProcedures buildBlockOnBlockOffMethodProcedures(
			EtsFuelUseMonitoringMethods etsFuelUseMonitoringMethods) {
		return EmpBlockOnBlockOffMethodProcedures
				.builder()
				.fuelConsumptionPerFlight(createProcedureForm(
						etsFuelUseMonitoringMethods.getProcedureDetailsTitle(),
						etsFuelUseMonitoringMethods.getProcedureDetailsReference(),
						etsFuelUseMonitoringMethods.getProcedureDetailsDescription(),
						etsFuelUseMonitoringMethods.getProcedureDetailsPost(),
						etsFuelUseMonitoringMethods.getProcedureDetailsLocation(),
						etsFuelUseMonitoringMethods.getProcedureDetailsSystem()))
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
	
	public static Map<String, String> getZeroFuelUpliftValuesForMigration() {
        return Map.of(
            "86220839-FD2D-4197-8C45-ACBD0159BA43", "Fuel uplift data is sent to FLT/FDWH and is allocated to each and every flight. Where an uplift covers more than one flight it is split across all relevant flights according to CORSIA SARPs, i.e. based on each flight proportion of total block hours the uplift covers. The block-time will be determined based on the OOOI (Out, Off, On, In messages sent by ACARS) which is common for the aircraft branch. If there is no uplift, the Fuel-Reporting from Flight deck will contain ‘0’ as uplift and there will be no invoices for this flight.",
            "01DB8A72-BE80-4742-9781-ACBD0159D35D", "Fuel uplift data that is sent to FLT/FDWH is allocated to each and every flight. Where an uplift covers more than one flight it is split across all relevant flights based on each flight proportion of total block-hours the uplift covers.",
            "597BBA5F-172A-4D08-9C06-ACBD0159DEE1", "Fuel uplift data that is sent to FLT/FDWH is allocated to each and every flight. Where an uplift covers more than one flight it is split across all relevant flights based on each flight proportion of total block-hours the uplift covers.",
            "B07EFCB3-D4F9-424E-BA59-ACBD0159EF95", "Fuel uplift data that is sent to FLT/FDWH is allocated to each and every flight. Where an uplift covers more than one flight it is split across all relevant flights according to CORSIA SARPs, i.e. based on each flight proportion of total block hours the uplift covers.",
            "116176C9-C7B5-47A1-8779-ACCA00FD0B61", "Fuel uplift data that is sent to FLT/FDWH is allocated to each and every flight. Where an uplift covers more than one flight it is split across all relevant flights according to CORSIA SARPs, i.e. based on each flight proportion of total block hours the uplift covers.",
            "9DE979B6-7E01-4629-875D-ADF800A77BF4", "For flights with no uplift, we will collect the information from the flight log in the ETS spreadsheet. The ETS spreadsheet is generated from the flight schedule, regardless of uplift. Block time is pulled from the software database after being audited from the flight logs to enable us to allocate fuel use for flights with no uplift.");
    }
	
}
