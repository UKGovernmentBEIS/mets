package uk.gov.pmrv.api.reporting.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.EmissionCalculationParams;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.RegionalInventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationFuelMeteringConditionType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationInventoryEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmissionCalculationParamsMapper {

    InventoryEmissionCalculationParamsDTO toInventoryEmissionCalculationParamsDTO(EmissionCalculationParams emissionCalculationParams);

    RegionalInventoryEmissionCalculationParamsDTO toRegionalInventoryEmissionCalculationParamsDTO(EmissionCalculationParams emissionCalculationParams,
        BigDecimal calculationFactor);

    InventoryEmissionCalculationParamsDTO toInventoryEmissionCalculationParams(CalculationInventoryEmissionCalculationParamValues values);

    RegionalInventoryEmissionCalculationParamsDTO toRegionalInventoryEmissionCalculationParams(CalculationInventoryEmissionCalculationParamValues values);

    RegionalInventoryEmissionCalculationParamsDTO toRegionalInventoryEmissionCalculationParamsDTO(RegionalInventoryEmissionCalculationParamsDTO values);

    @Mapping(target = "containsBiomass", source = "biomassPercentages.contains")
    @Mapping(target = "biomassPercentage", source = "biomassPercentages.biomassPercentage")
    @Mapping(target = "activityData", source = "activityDataCalculationMethod.activityData")
    @Mapping(target = "activityDataMeasurementUnit", source = "activityDataCalculationMethod.measurementUnit")
    EmissionsCalculationParamsDTO toEmissionsCalculationParams(RegionalInventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParams,
                                                               CalculationActivityDataCalculationMethod activityDataCalculationMethod,
                                                               BiomassPercentages biomassPercentages,
                                                               SourceStreamType sourceStreamType,
                                                               CalculationFuelMeteringConditionType fuelMeteringConditionType);

    @Mapping(target = "containsBiomass", source = "biomassPercentages.contains")
    @Mapping(target = "biomassPercentage", source = "biomassPercentages.biomassPercentage")
    @Mapping(target = "activityData", source = "activityDataCalculationMethod.activityData")
    @Mapping(target = "activityDataMeasurementUnit", source = "activityDataCalculationMethod.measurementUnit")
    EmissionsCalculationParamsDTO toEmissionsCalculationParams(InventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParams,
                                                               CalculationActivityDataCalculationMethod activityDataCalculationMethod,
                                                               BiomassPercentages biomassPercentages,
                                                               SourceStreamType sourceStreamType);

    @Mapping(target = "containsBiomass", source = "biomassPercentages.contains")
    @Mapping(target = "biomassPercentage", source = "biomassPercentages.biomassPercentage")
    @Mapping(target = "activityData", source = "activityDataCalculationMethod.activityData")
    @Mapping(target = "activityDataMeasurementUnit", source = "activityDataCalculationMethod.measurementUnit")
    EmissionsCalculationParamsDTO toEmissionsCalculationParams(CalculationManualEmissionCalculationParamValues manualEmissionCalculationParams,
                                                               CalculationActivityDataCalculationMethod activityDataCalculationMethod,
                                                               BiomassPercentages biomassPercentages,
                                                               SourceStreamType sourceStreamType);

    @Mapping(target = "reportableEmissions", source = "totalReportableEmissions")
    @Mapping(target = "sustainableBiomassEmissions", source = "totalSustainableBiomassEmissions")
    EmissionsCalculationDTO toEmissionsCalculationDTO(CalculationEmissionCalculationParamValues emissionCalculationParamValues);

    @Mapping(target = "containsBiomass", source = "biomassPercentages.contains")
    @Mapping(target = "biomassPercentage", source = "biomassPercentages.biomassPercentage")
    MeasurementEmissionsCalculationParamsDTO toMeasurementCO2EmissionsCalculationParamsDTO(
        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission);


    MeasurementEmissionsCalculationDTO toMeasurementEmissionsCalculationDTO(
        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission);

    @Mapping(target = "containsBiomass", source = "biomassPercentages.contains")
    @Mapping(target = "biomassPercentage", source = "biomassPercentages.biomassPercentage")
    MeasurementEmissionsCalculationParamsDTO toMeasurementN2OEmissionsCalculationParamsDTO(
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission);


    MeasurementEmissionsCalculationDTO toMeasurementN2OEmissionsCalculationDTO(
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission);

    @Mapping(target = "containsBiomass", source = "biomass.contains")
    @Mapping(target = "totalNonSustainableBiomassEmissions", source = "biomass.totalNonSustainableBiomassEmissions")
    FallbackEmissionsCalculationParamsDTO toFallbackEmissionsCalculationParamsDTO(
        FallbackEmissions fallbackEmission);

    FallbackEmissionsCalculationDTO toFallbackEmissionsCalculationDTO(
        FallbackEmissions fallbackEmission);


    @Mapping(source = "pfcSourceStreamEmissionCalculationMethodData.calculationMethod", target = "calculationMethod")
    PfcEmissionsCalculationParamsDTO toPfcEmissionsCalculationParamsDTO(
        PfcSourceStreamEmission pfcSourceStreamEmission
    );

    PfcEmissionsCalculationDTO toPfcEmissionsCalculationDTO(
        PfcSourceStreamEmission pfcSourceStreamEmission
    );
}
