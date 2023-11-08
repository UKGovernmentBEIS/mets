package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFractionTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationBiomassFractionMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AerCalculationSourceStreamBiomassMonitoringTierValidatorTest {

    private final AerCalculationSourceStreamBiomassMonitoringTierValidator validator =
        new AerCalculationSourceStreamBiomassMonitoringTierValidator();

    @Test
    void validate_valid_source_source_stream_contains_biomass() {
        List<CalculationParameterMonitoringTier> calculationParameterMonitoringTiers = List.of(
            CalculationActivityDataMonitoringTier.builder().
                type(CalculationParameterType.ACTIVITY_DATA)
                .tier(CalculationActivityDataTier.TIER_3)
                .build(),
            CalculationEmissionFactorMonitoringTier.builder()
                .type(CalculationParameterType.EMISSION_FACTOR)
                .tier(CalculationEmissionFactorTier.TIER_2)
                .build(),
            CalculationBiomassFractionMonitoringTier.builder()
                .type(CalculationParameterType.BIOMASS_FRACTION)
                .tier(CalculationBiomassFractionTier.NO_TIER)
                .build()
        );
        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream("1")
            .parameterMonitoringTiers(calculationParameterMonitoringTiers)
            .biomassPercentages(BiomassPercentages.builder().contains(true).build())
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_valid_source_source_stream_does_not_contain_biomass() {
        List<CalculationParameterMonitoringTier> calculationParameterMonitoringTiers = List.of(
            CalculationActivityDataMonitoringTier.builder().
                type(CalculationParameterType.ACTIVITY_DATA)
                .tier(CalculationActivityDataTier.TIER_3)
                .build(),
            CalculationEmissionFactorMonitoringTier.builder()
                .type(CalculationParameterType.EMISSION_FACTOR)
                .tier(CalculationEmissionFactorTier.TIER_2)
                .build()
        );
        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream("1")
            .parameterMonitoringTiers(calculationParameterMonitoringTiers)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_biomass_monitoring_tier_diff_should_exist() {
        List<CalculationParameterMonitoringTier> calculationParameterMonitoringTiers = List.of(
            CalculationActivityDataMonitoringTier.builder().
                type(CalculationParameterType.ACTIVITY_DATA)
                .tier(CalculationActivityDataTier.TIER_3)
                .build(),
            CalculationEmissionFactorMonitoringTier.builder()
                .type(CalculationParameterType.EMISSION_FACTOR)
                .tier(CalculationEmissionFactorTier.TIER_2)
                .build()
        );
        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream("1")
            .parameterMonitoringTiers(calculationParameterMonitoringTiers)
            .biomassPercentages(BiomassPercentages.builder().contains(true).build())
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INVALID_BIOMASS_FRACTION_MONITORING_TIER.getMessage(), violation.getMessage());
    }

    @Test
    void validate_biomass_monitoring_tier_diff_should_not_exist() {
        List<CalculationParameterMonitoringTier> calculationParameterMonitoringTiers = List.of(
            CalculationActivityDataMonitoringTier.builder().
                type(CalculationParameterType.ACTIVITY_DATA)
                .tier(CalculationActivityDataTier.TIER_3)
                .build(),
            CalculationEmissionFactorMonitoringTier.builder()
                .type(CalculationParameterType.EMISSION_FACTOR)
                .tier(CalculationEmissionFactorTier.TIER_2)
                .build(),
            CalculationBiomassFractionMonitoringTier.builder()
                .type(CalculationParameterType.BIOMASS_FRACTION)
                .tier(CalculationBiomassFractionTier.NO_TIER)
                .build()
        );
        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream("1")
            .parameterMonitoringTiers(calculationParameterMonitoringTiers)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INVALID_BIOMASS_FRACTION_MONITORING_TIER.getMessage(), violation.getMessage());
    }
}