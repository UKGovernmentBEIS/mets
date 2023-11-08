package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReasonType;

class AerCalculationSourceStreamParamMonitoringTierDiffReasonValidatorTest {

    private final AerCalculationSourceStreamParamMonitoringTierDiffReasonValidator validator =
        new AerCalculationSourceStreamParamMonitoringTierDiffReasonValidator();

    @Test
    void validate_valid() {
        String sourceStreamId = "1";
        String calculationEmissionSourceStreamId = UUID.randomUUID().toString();

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
            .id(calculationEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
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
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
                MonitoringApproachMonitoringTiers.builder()
                        .calculationSourceStreamParamMonitoringTiers(Map.of(calculationEmissionSourceStreamId, calculationParameterMonitoringTiers))
                        .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
                .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
                .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation>  aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_diff_reason_should_not_exist() {
        String sourceStreamId = "1";
        String calculationEmissionSourceStreamId = UUID.randomUUID().toString();

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
            .id(calculationEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTiers(calculationParameterMonitoringTiers)
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .type(ParameterMonitoringTierDiffReasonType.DATA_GAP)
                .reason("reason")
                .build())
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
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationSourceStreamParamMonitoringTiers(Map.of(calculationEmissionSourceStreamId, calculationParameterMonitoringTiers))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation>  aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON.getMessage(), violation.getMessage());
    }

    @Test
    void validate_diff_reason_should_exist() {
        String sourceStreamId = "1";
        String calculationEmissionSourceStreamId = UUID.randomUUID().toString();

        List<CalculationParameterMonitoringTier> sourceStreamCalculationParameterMonitoringTiers = List.of(
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
            .id(calculationEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTiers(sourceStreamCalculationParameterMonitoringTiers)
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
        List<CalculationParameterMonitoringTier> permitSourceStreamCalculationParameterMonitoringTiers = List.of(
            CalculationActivityDataMonitoringTier.builder().
                type(CalculationParameterType.ACTIVITY_DATA)
                .tier(CalculationActivityDataTier.TIER_3)
                .build(),
            CalculationEmissionFactorMonitoringTier.builder()
                .type(CalculationParameterType.EMISSION_FACTOR)
                .tier(CalculationEmissionFactorTier.NO_TIER)
                .build()
        );
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationSourceStreamParamMonitoringTiers(Map.of(calculationEmissionSourceStreamId, permitSourceStreamCalculationParameterMonitoringTiers))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation>  aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON.getMessage(), violation.getMessage());
    }

    @Test
    void validate_diff_reason_notifications_should_not_exist() {
        String sourceStreamId = "1";
        String calculationEmissionSourceStreamId = UUID.randomUUID().toString();

        List<CalculationParameterMonitoringTier> sourceStreamCalculationParameterMonitoringTiers = List.of(
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
            .id(calculationEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTiers(sourceStreamCalculationParameterMonitoringTiers)
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .type(ParameterMonitoringTierDiffReasonType.OTHER)
                .reason("reason")
                .relatedNotifications(List.of("AEMN-209"))
                .build())
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
        List<CalculationParameterMonitoringTier> permitSourceStreamCalculationParameterMonitoringTiers = List.of(
            CalculationActivityDataMonitoringTier.builder().
                type(CalculationParameterType.ACTIVITY_DATA)
                .tier(CalculationActivityDataTier.TIER_3)
                .build(),
            CalculationEmissionFactorMonitoringTier.builder()
                .type(CalculationParameterType.EMISSION_FACTOR)
                .tier(CalculationEmissionFactorTier.NO_TIER)
                .build()
        );
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationSourceStreamParamMonitoringTiers(Map.of(calculationEmissionSourceStreamId, permitSourceStreamCalculationParameterMonitoringTiers))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation>  aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS_SHOULD_NOT_EXIST.getMessage(), violation.getMessage());
    }

    @Test
    void validate_diff_reason_invalid_notifications() {
        String sourceStreamId = "1";
        String calculationEmissionSourceStreamId = UUID.randomUUID().toString();

        List<CalculationParameterMonitoringTier> sourceStreamCalculationParameterMonitoringTiers = List.of(
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
            .id(calculationEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTiers(sourceStreamCalculationParameterMonitoringTiers)
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .type(ParameterMonitoringTierDiffReasonType.DATA_GAP)
                .reason("reason")
                .relatedNotifications(List.of("AEMN-209"))
                .build())
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
        List<CalculationParameterMonitoringTier> permitSourceStreamCalculationParameterMonitoringTiers = List.of(
            CalculationActivityDataMonitoringTier.builder().
                type(CalculationParameterType.ACTIVITY_DATA)
                .tier(CalculationActivityDataTier.TIER_3)
                .build(),
            CalculationEmissionFactorMonitoringTier.builder()
                .type(CalculationParameterType.EMISSION_FACTOR)
                .tier(CalculationEmissionFactorTier.NO_TIER)
                .build()
        );
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationSourceStreamParamMonitoringTiers(Map.of(calculationEmissionSourceStreamId, permitSourceStreamCalculationParameterMonitoringTiers))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation>  aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS.getMessage(), violation.getMessage());
    }

    @Test
    void validate_valid_source_stream_not_transfered_from_permit() {
        String sourceStreamId = "1";
        String calculationEmissionSourceStreamId = UUID.randomUUID().toString();

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
            .sourceStream(sourceStreamId)
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
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationSourceStreamParamMonitoringTiers(Map.of(calculationEmissionSourceStreamId, calculationParameterMonitoringTiers))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation>  aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }
}