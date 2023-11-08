package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNationalInventoryDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOxidationFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AerCalculationSourceStreamParamCalculationMethodValidatorTest {

    private final AerCalculationSourceStreamParamCalculationMethodValidator validator =
        new AerCalculationSourceStreamParamCalculationMethodValidator();

    @Test
    void validate_valid() {
        String sourceStreamId = "1";
        SourceStream sourceStream = SourceStream.builder().id(sourceStreamId).type(SourceStreamType.COMBUSTION_SCRUBBING_CARBONATE).build();
        SourceStreams sourceStreams = SourceStreams.builder().sourceStreams(List.of(sourceStream)).build();

        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream(sourceStreamId)
            .parameterMonitoringTiers(List.of(
                CalculationActivityDataMonitoringTier.builder()
                    .type(CalculationParameterType.ACTIVITY_DATA)
                    .tier(CalculationActivityDataTier.TIER_3)
                    .build(),
                CalculationNetCalorificValueMonitoringTier.builder().
                    type(CalculationParameterType.NET_CALORIFIC_VALUE)
                    .tier(CalculationNetCalorificValueTier.TIER_2A)
                    .build(),
                CalculationEmissionFactorMonitoringTier.builder()
                    .type(CalculationParameterType.EMISSION_FACTOR)
                    .tier(CalculationEmissionFactorTier.TIER_2)
                    .build(),
                CalculationOxidationFactorMonitoringTier.builder()
                    .type(CalculationParameterType.OXIDATION_FACTOR)
                    .tier(CalculationOxidationFactorTier.NO_TIER)
                    .build())
            )
            .parameterCalculationMethod(CalculationNationalInventoryDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                .build())
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();

        Aer aer = Aer.builder().sourceStreams(sourceStreams).monitoringApproachEmissions(monitoringApproachEmissions).build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_invalid_combination_of_oxidation_factor_and_calc_method() {
        String sourceStreamId = "1";
        SourceStream sourceStream = SourceStream.builder().id(sourceStreamId).type(SourceStreamType.COMBUSTION_SCRUBBING_CARBONATE).build();
        SourceStreams sourceStreams = SourceStreams.builder().sourceStreams(List.of(sourceStream)).build();

        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream(sourceStreamId)
            .parameterMonitoringTiers(List.of(
                CalculationActivityDataMonitoringTier.builder()
                    .type(CalculationParameterType.ACTIVITY_DATA)
                    .tier(CalculationActivityDataTier.TIER_3)
                    .build(),
                CalculationNetCalorificValueMonitoringTier.builder().
                    type(CalculationParameterType.NET_CALORIFIC_VALUE)
                    .tier(CalculationNetCalorificValueTier.TIER_2A)
                    .build(),
                CalculationEmissionFactorMonitoringTier.builder()
                    .type(CalculationParameterType.EMISSION_FACTOR)
                    .tier(CalculationEmissionFactorTier.TIER_2)
                    .build())
            )
            .parameterCalculationMethod(CalculationNationalInventoryDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                .build())
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();

        Aer aer = Aer.builder().sourceStreams(sourceStreams).monitoringApproachEmissions(monitoringApproachEmissions).build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INVALID_PARAMETER_CALCULATION_METHOD.getMessage(), violation.getMessage());
    }

    @Test
    void validate_invalid_combination_of_ncv_value_and_calc_method() {
        String sourceStreamId = "1";
        SourceStream sourceStream = SourceStream.builder().id(sourceStreamId).type(SourceStreamType.COMBUSTION_SCRUBBING_CARBONATE).build();
        SourceStreams sourceStreams = SourceStreams.builder().sourceStreams(List.of(sourceStream)).build();

        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream(sourceStreamId)
            .parameterMonitoringTiers(List.of(
                CalculationActivityDataMonitoringTier.builder()
                    .type(CalculationParameterType.ACTIVITY_DATA)
                    .tier(CalculationActivityDataTier.TIER_3)
                    .build(),
                CalculationNetCalorificValueMonitoringTier.builder().
                    type(CalculationParameterType.NET_CALORIFIC_VALUE)
                    .tier(CalculationNetCalorificValueTier.TIER_1)
                    .build(),
                CalculationEmissionFactorMonitoringTier.builder()
                    .type(CalculationParameterType.EMISSION_FACTOR)
                    .tier(CalculationEmissionFactorTier.TIER_2)
                    .build(),
                CalculationOxidationFactorMonitoringTier.builder()
                    .type(CalculationParameterType.OXIDATION_FACTOR)
                    .tier(CalculationOxidationFactorTier.NO_TIER)
                    .build())
            )
            .parameterCalculationMethod(CalculationNationalInventoryDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                .build())
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();

        Aer aer = Aer.builder().sourceStreams(sourceStreams).monitoringApproachEmissions(monitoringApproachEmissions).build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INVALID_PARAMETER_CALCULATION_METHOD.getMessage(), violation.getMessage());
    }

    @Test
    void validate_invalid_combination_of_emission_factor_value_and_calc_method() {
        String sourceStreamId = "1";
        SourceStream sourceStream = SourceStream.builder().id(sourceStreamId).type(SourceStreamType.COMBUSTION_SCRUBBING_CARBONATE).build();
        SourceStreams sourceStreams = SourceStreams.builder().sourceStreams(List.of(sourceStream)).build();

        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream(sourceStreamId)
            .parameterMonitoringTiers(List.of(
                CalculationActivityDataMonitoringTier.builder()
                    .type(CalculationParameterType.ACTIVITY_DATA)
                    .tier(CalculationActivityDataTier.TIER_3)
                    .build(),
                CalculationNetCalorificValueMonitoringTier.builder().
                    type(CalculationParameterType.NET_CALORIFIC_VALUE)
                    .tier(CalculationNetCalorificValueTier.TIER_2A)
                    .build(),
                CalculationEmissionFactorMonitoringTier.builder()
                    .type(CalculationParameterType.EMISSION_FACTOR)
                    .tier(CalculationEmissionFactorTier.TIER_3)
                    .build(),
                CalculationOxidationFactorMonitoringTier.builder()
                    .type(CalculationParameterType.OXIDATION_FACTOR)
                    .tier(CalculationOxidationFactorTier.NO_TIER)
                    .build())
            )
            .parameterCalculationMethod(CalculationNationalInventoryDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.REGIONAL_DATA)
                .build())
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();

        Aer aer = Aer.builder().sourceStreams(sourceStreams).monitoringApproachEmissions(monitoringApproachEmissions).build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INVALID_PARAMETER_CALCULATION_METHOD.getMessage(), violation.getMessage());
    }
}