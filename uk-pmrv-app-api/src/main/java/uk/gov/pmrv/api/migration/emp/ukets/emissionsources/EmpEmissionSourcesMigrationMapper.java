package uk.gov.pmrv.api.migration.emp.ukets.emissionsources;

import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.migration.emp.ukets.emissionsources.aircrafttypedetails.EtsAircraftTypeDetails;
import uk.gov.pmrv.api.migration.emp.ukets.emissionsources.emissionsourcesdetails.EtsEmpEmissionSources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmpEmissionSourcesMigrationMapper {

	public static EmpEmissionSources toEmpEmissionSources(
			EtsEmpEmissionSources etsEmpEmissionSources) {
		
		return EmpEmissionSources.builder()
				.additionalAircraftMonitoringApproach(createProcedureForm(
						etsEmpEmissionSources.getProcedureDetailsAdditionalAircraftTitle(),
						etsEmpEmissionSources.getProcedureDetailsAdditionalAircraftReference(),
						etsEmpEmissionSources.getProcedureDetailsAdditionalAircraftDescription(),
						etsEmpEmissionSources.getProcedureDetailsAdditionalAircraftPost(),
						etsEmpEmissionSources.getProcedureDetailsAdditionalAircraftLocation(),
						etsEmpEmissionSources.getProcedureDetailsAdditionalAircraftSystem()))
				.multipleFuelConsumptionMethodsExplanation(etsEmpEmissionSources.getApproachJustification())
                .build();	
	}
	
	public static Set<AircraftTypeDetails> toAircraftTypeDetails(
			List<EtsAircraftTypeDetails> etsAircraftTypeDetails) {
		Set<AircraftTypeDetails> aircraftTypeDetails = new HashSet<>();
		
		etsAircraftTypeDetails.forEach(entry -> aircraftTypeDetails.add(
				AircraftTypeDetails.builder()
				.aircraftTypeInfo(AircraftTypeInfo
						.builder()
						.manufacturer(entry.getHMake())
						.model(entry.getHModel())
						.designatorType(entry.getHDesignator())
						.build())
				.subtype(entry.getGenericAircraftSubtype())
				.numberOfAircrafts(entry.getNumberOfAircraft())
				.fuelTypes(constructFuelTypes(entry))
				.isCurrentlyUsed(true)
				.fuelConsumptionMeasuringMethod(constructMethodology(entry.getMethodology()))
                .build()));
		
		return aircraftTypeDetails;
	}
	
	public static Set<AircraftTypeDetails> toAdditionalAircraftTypeDetails(
			List<EtsAircraftTypeDetails> etsAircraftTypeDetails) {
		Set<AircraftTypeDetails> additionalAircraftTypeDetails = new HashSet<>();
		toAircraftTypeDetails(etsAircraftTypeDetails).forEach(entry -> {
			entry.setIsCurrentlyUsed(false);
			additionalAircraftTypeDetails.add(entry);
		});
		return additionalAircraftTypeDetails;
	}

	private static List<FuelType> constructFuelTypes(EtsAircraftTypeDetails etsAircraftTypeDetails) {
		List<FuelType> fuelTypes = new ArrayList<FuelType>();
		if (etsAircraftTypeDetails.getChkJetKerosene()) {
			fuelTypes.add(FuelType.JET_KEROSENE);
		}
		if (etsAircraftTypeDetails.getChkJetGasoline()) {
			fuelTypes.add(FuelType.JET_GASOLINE);
		}
		if (etsAircraftTypeDetails.getChkAviationGasoline()) {
			fuelTypes.add(FuelType.AVIATION_GASOLINE);
		}
		
		return fuelTypes.isEmpty() ? List.of(FuelType.OTHER) : fuelTypes;
	}

	private static FuelConsumptionMeasuringMethod constructMethodology(String methodology) {
		FuelConsumptionMeasuringMethod fuelConsumptionMeasuringMethod = null;
		if (methodology != null) {
			if ("Off-Block/On-Block".equals(methodology)) {
				fuelConsumptionMeasuringMethod = FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF;
			} else if ("Fuel Uplift".equals(methodology)) {
				fuelConsumptionMeasuringMethod = FuelConsumptionMeasuringMethod.FUEL_UPLIFT;
			} else if ("Method A".equals(methodology)){
				fuelConsumptionMeasuringMethod = FuelConsumptionMeasuringMethod.METHOD_A;
			} else if ("Method B".equals(methodology)){
				fuelConsumptionMeasuringMethod = FuelConsumptionMeasuringMethod.METHOD_B;
			}
		}
		return fuelConsumptionMeasuringMethod;
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
