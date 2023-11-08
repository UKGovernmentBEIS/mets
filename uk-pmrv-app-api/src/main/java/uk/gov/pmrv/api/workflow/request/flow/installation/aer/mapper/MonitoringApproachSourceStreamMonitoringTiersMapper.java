package uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PermitOriginatedCalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class MonitoringApproachSourceStreamMonitoringTiersMapper {

    public MonitoringApproachMonitoringTiers transformToMonitoringApproachMonitoringTiers(
        MonitoringApproachEmissions monitoringApproachEmissions) {
        Map<MonitoringApproachType, AerMonitoringApproachEmissions> aerMonitoringApproachEmissions =
            monitoringApproachEmissions.getMonitoringApproachEmissions();

        MonitoringApproachMonitoringTiers monitoringApproachMonitoringTiers = new MonitoringApproachMonitoringTiers();

        if (aerMonitoringApproachEmissions.get(MonitoringApproachType.CALCULATION_CO2) != null) {
            monitoringApproachMonitoringTiers.setCalculationSourceStreamParamMonitoringTiers(
                transformCalculationEmissionsToSourceStreamParamMonitoringTiersList(aerMonitoringApproachEmissions.get(MonitoringApproachType.CALCULATION_CO2))
            );
        }
        if (aerMonitoringApproachEmissions.get(MonitoringApproachType.MEASUREMENT_CO2) != null) {
            monitoringApproachMonitoringTiers.setMeasurementCO2EmissionPointParamMonitoringTiers(
                transformMeasurementCO2EmissionsToEmissionPointParamMonitoringTiersList(
                    aerMonitoringApproachEmissions.get(MonitoringApproachType.MEASUREMENT_CO2))
            );
        }
        if (aerMonitoringApproachEmissions.get(MonitoringApproachType.MEASUREMENT_N2O) != null) {
            monitoringApproachMonitoringTiers.setMeasurementN2OEmissionPointParamMonitoringTiers(
                transformMeasurementN2OEmissionsToEmissionPointParamMonitoringTiersList(
                    aerMonitoringApproachEmissions.get(MonitoringApproachType.MEASUREMENT_N2O))
            );
        }
        if (aerMonitoringApproachEmissions.get(MonitoringApproachType.CALCULATION_PFC) != null) {
            monitoringApproachMonitoringTiers.setCalculationPfcSourceStreamParamMonitoringTiers(
                transformPfcEmissions(aerMonitoringApproachEmissions.get(MonitoringApproachType.CALCULATION_PFC))
            );
        }

        return monitoringApproachMonitoringTiers;
    }

    private Map<String, List<CalculationParameterMonitoringTier>> transformCalculationEmissionsToSourceStreamParamMonitoringTiersList(
        AerMonitoringApproachEmissions aerMonitoringApproachEmissions) {
        List<CalculationSourceStreamEmission> calculationSourceStreamEmissions =
            ((CalculationOfCO2Emissions) aerMonitoringApproachEmissions).getSourceStreamEmissions();
        Map<String, List<CalculationParameterMonitoringTier>> sourceStreamParamMonitoringTiers = new HashMap<>();

        calculationSourceStreamEmissions.forEach(sourceStreamEmission ->
            sourceStreamParamMonitoringTiers.put(sourceStreamEmission.getId(),
                sourceStreamEmission.getParameterMonitoringTiers())
        );

        return sourceStreamParamMonitoringTiers;
    }

    private Map<String, MeasurementOfCO2MeasuredEmissionsTier> transformMeasurementCO2EmissionsToEmissionPointParamMonitoringTiersList(
        AerMonitoringApproachEmissions aerMonitoringApproachEmissions) {
        List<MeasurementCO2EmissionPointEmission> measurementEmissionPointEmissions =
            ((MeasurementOfCO2Emissions) aerMonitoringApproachEmissions).getEmissionPointEmissions();
        Map<String, MeasurementOfCO2MeasuredEmissionsTier> emissionPointParamMonitoringTiers = new HashMap<>();

        measurementEmissionPointEmissions.forEach(measurementEmissionPointEmission ->
            emissionPointParamMonitoringTiers.put(measurementEmissionPointEmission.getId(),
                measurementEmissionPointEmission.getTier())
        );

        return emissionPointParamMonitoringTiers;
    }

    private Map<String, MeasurementOfN2OMeasuredEmissionsTier> transformMeasurementN2OEmissionsToEmissionPointParamMonitoringTiersList(
        AerMonitoringApproachEmissions aerMonitoringApproachEmissions) {
        List<MeasurementN2OEmissionPointEmission> measurementEmissionPointEmissions =
            ((MeasurementOfN2OEmissions) aerMonitoringApproachEmissions).getEmissionPointEmissions();
        Map<String, MeasurementOfN2OMeasuredEmissionsTier> emissionPointParamMonitoringTiers = new HashMap<>();

        measurementEmissionPointEmissions.forEach(measurementEmissionPointEmission ->
            emissionPointParamMonitoringTiers.put(measurementEmissionPointEmission.getId(),
                measurementEmissionPointEmission.getTier())
        );

        return emissionPointParamMonitoringTiers;
    }

    private Map<String, PermitOriginatedCalculationPfcParameterMonitoringTier> transformPfcEmissions(AerMonitoringApproachEmissions aerMonitoringApproachEmissions) {
        List<PfcSourceStreamEmission> pfcSourceStreamEmissions =
            ((CalculationOfPfcEmissions) aerMonitoringApproachEmissions).getSourceStreamEmissions();
        Map<String, PermitOriginatedCalculationPfcParameterMonitoringTier> sourceStreamPfcEmissionFactorTier = new HashMap<>();

        pfcSourceStreamEmissions.forEach(pfcSourceStreamEmission ->
            sourceStreamPfcEmissionFactorTier.put(pfcSourceStreamEmission.getId(),
                PermitOriginatedCalculationPfcParameterMonitoringTier.builder()
                    .activityDataTier(pfcSourceStreamEmission.getParameterMonitoringTier().getActivityDataTier())
                    .emissionFactorTier(pfcSourceStreamEmission.getParameterMonitoringTier().getEmissionFactorTier())
                    .massBalanceApproachUsed(pfcSourceStreamEmission.isMassBalanceApproachUsed())
                    .build())
        );

        return sourceStreamPfcEmissionFactorTier;
    }
}
