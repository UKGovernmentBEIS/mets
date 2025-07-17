package uk.gov.pmrv.api.migration.emp.corsia.emissionsources;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.enumeration.FuelTypeCorsia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmpEmissionSourcesCorsiaMigrationMapper {
	
	public static EmpEmissionSourcesCorsia toEmissionSources(
			List<EtsEmissionSourcesCorsia> etsEmissionSources) {
		Set<AircraftTypeDetailsCorsia> aircraftTypeDetails = new HashSet<>();
		
		etsEmissionSources.forEach(entry -> aircraftTypeDetails.add(
				AircraftTypeDetailsCorsia.builder()
				.aircraftTypeInfo(AircraftTypeInfo
						.builder()
						.manufacturer(entry.getHMake())
						.model(entry.getHModel())
						.designatorType(entry.getHDesignator())
						.build())
				.subtype(entry.getGenericAircraftSubtype())
				.numberOfAircrafts(entry.getNumberOfAircraft())
				.fuelTypes(constructFuelTypes(entry))
				.fuelConsumptionMeasuringMethod(constructMethodology(entry.getCorsiaMethodology()))
                .build()));
		
		return EmpEmissionSourcesCorsia.builder()
				.aircraftTypes(aircraftTypeDetails)
				.multipleFuelConsumptionMethodsExplanation(null)
				.build();
	}
	
	private static List<FuelTypeCorsia> constructFuelTypes(EtsEmissionSourcesCorsia etsEmissionSourcesCorsia) {
		List<FuelTypeCorsia> fuelTypes = new ArrayList<FuelTypeCorsia>();
		if (etsEmissionSourcesCorsia.getChkJetKerosene()) {
			fuelTypes.add(FuelTypeCorsia.JET_KEROSENE);
		}
		if (etsEmissionSourcesCorsia.getChkJetGasoline()) {
			fuelTypes.add(FuelTypeCorsia.JET_GASOLINE);
		}
		if (etsEmissionSourcesCorsia.getChkAviationGasoline()) {
			fuelTypes.add(FuelTypeCorsia.AVIATION_GASOLINE);
		}
		
		return fuelTypes;
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
}
