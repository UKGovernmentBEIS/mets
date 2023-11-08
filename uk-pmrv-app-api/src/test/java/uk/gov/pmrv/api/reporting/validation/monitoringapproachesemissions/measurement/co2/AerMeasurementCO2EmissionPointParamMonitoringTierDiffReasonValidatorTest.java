package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.co2;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReasonType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidatorTest {

    private final AerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator validator =
        new AerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator();

    @Test
    void validate_valid() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
            MeasurementCO2EmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_2)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        MeasurementOfCO2Emissions measurementOfCO2Emissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementOfCO2Emissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementCO2EmissionPointParamMonitoringTiers(Map.of(measurementEmissionPointId,
                    MeasurementOfCO2MeasuredEmissionsTier.TIER_2))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_diff_reason_should_not_exist() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
            MeasurementCO2EmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_2)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .reason("some reason")
                .type(ParameterMonitoringTierDiffReasonType.DATA_GAP)
                .build())
            .build();
        MeasurementOfCO2Emissions measurementOfCO2Emissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementOfCO2Emissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementCO2EmissionPointParamMonitoringTiers(Map.of(measurementEmissionPointId,
                    MeasurementOfCO2MeasuredEmissionsTier.TIER_2))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("MeasurementOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON.getMessage(),
            violation.getMessage());
    }

    @Test
    void validate_diff_reason_should_exist() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
            MeasurementCO2EmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_2)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        MeasurementOfCO2Emissions measurementOfCO2Emissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementOfCO2Emissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementCO2EmissionPointParamMonitoringTiers(Map.of(emissionPointId,
                    MeasurementOfCO2MeasuredEmissionsTier.TIER_4))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("MeasurementOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON.getMessage(),
            violation.getMessage());
    }

    @Test
    void validate_diff_reason_notifications_should_not_exist() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
            MeasurementCO2EmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_2)
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .reason("some reason")
                .type(ParameterMonitoringTierDiffReasonType.OTHER)
                .relatedNotifications(List.of("Some notification"))
                .build())
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        MeasurementOfCO2Emissions measurementOfCO2Emissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementOfCO2Emissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementCO2EmissionPointParamMonitoringTiers(Map.of(emissionPointId,
                    MeasurementOfCO2MeasuredEmissionsTier.TIER_3))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("MeasurementOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS_SHOULD_NOT_EXIST.getMessage(), violation.getMessage());
    }

    @Test
    void validate_diff_reason_invalid_notifications() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
            MeasurementCO2EmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_4)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .reason("some reason")
                .type(ParameterMonitoringTierDiffReasonType.DATA_GAP)
                .relatedNotifications(List.of("AEM-259"))
                .build())
            .build();
        MeasurementOfCO2Emissions measurementOfCO2Emissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementOfCO2Emissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementCO2EmissionPointParamMonitoringTiers(Map.of(emissionPointId,
                    MeasurementOfCO2MeasuredEmissionsTier.TIER_2))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("MeasurementOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS.getMessage(), violation.getMessage());
    }

    @Test
    void validate_valid_emission_point_not_transferred_from_permit() {
        String emissionPointId = "1";
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
            MeasurementCO2EmissionPointEmission.builder()
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_2)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        MeasurementOfCO2Emissions measurementOfCO2Emissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementOfCO2Emissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementCO2EmissionPointParamMonitoringTiers(Map.of(emissionPointId,
                    MeasurementOfCO2MeasuredEmissionsTier.TIER_2))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }
}