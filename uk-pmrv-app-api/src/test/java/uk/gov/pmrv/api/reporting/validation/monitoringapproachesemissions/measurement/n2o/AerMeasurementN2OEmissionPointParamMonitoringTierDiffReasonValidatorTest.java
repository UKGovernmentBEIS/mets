package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.n2o;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReasonType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AerMeasurementN2OEmissionPointParamMonitoringTierDiffReasonValidatorTest {

    private final AerMeasurementN2OEmissionPointParamMonitoringTierDiffReasonValidator validator =
        new AerMeasurementN2OEmissionPointParamMonitoringTierDiffReasonValidator();

    @Test
    void validate_valid() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_2)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        MeasurementOfN2OEmissions measurementOfN2OEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_N2O, measurementOfN2OEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementN2OEmissionPointParamMonitoringTiers(Map.of(measurementEmissionPointId,
                    MeasurementOfN2OMeasuredEmissionsTier.TIER_2))
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

        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_2)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .reason("some reason")
                .type(ParameterMonitoringTierDiffReasonType.DATA_GAP)
                .build())
            .build();
        MeasurementOfN2OEmissions measurementOfN2OEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_N2O, measurementOfN2OEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementN2OEmissionPointParamMonitoringTiers(Map.of(measurementEmissionPointId,
                    MeasurementOfN2OMeasuredEmissionsTier.TIER_2))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("MeasurementOfN2OEmissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON.getMessage(),
            violation.getMessage());
    }

    @Test
    void validate_diff_reason_should_exist() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_2)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        MeasurementOfN2OEmissions measurementOfCO2Emissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementOfCO2Emissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementN2OEmissionPointParamMonitoringTiers(Map.of(emissionPointId,
                    MeasurementOfN2OMeasuredEmissionsTier.TIER_3))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("MeasurementOfN2OEmissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON.getMessage(),
            violation.getMessage());
    }

    @Test
    void validate_diff_reason_notifications_should_not_exist() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_2)
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .reason("some reason")
                .type(ParameterMonitoringTierDiffReasonType.OTHER)
                .relatedNotifications(List.of("Some notification"))
                .build())
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        MeasurementOfN2OEmissions measurementOfCO2Emissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_N2O, measurementOfCO2Emissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementN2OEmissionPointParamMonitoringTiers(Map.of(emissionPointId,
                    MeasurementOfN2OMeasuredEmissionsTier.TIER_3))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("MeasurementOfN2OEmissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS_SHOULD_NOT_EXIST.getMessage(), violation.getMessage());
    }

    @Test
    void validate_diff_reason_invalid_notifications() {
        String emissionPointId = "1";
        String measurementEmissionPointId = UUID.randomUUID().toString();
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .id(measurementEmissionPointId)
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_3)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .reason("some reason")
                .type(ParameterMonitoringTierDiffReasonType.DATA_GAP)
                .relatedNotifications(List.of("AEM-259"))
                .build())
            .build();
        MeasurementOfN2OEmissions measurementOfN2OEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_N2O, measurementOfN2OEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementN2OEmissionPointParamMonitoringTiers(Map.of(emissionPointId,
                    MeasurementOfN2OMeasuredEmissionsTier.TIER_2))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("MeasurementOfN2OEmissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS.getMessage(), violation.getMessage());
    }

    @Test
    void validate_valid_emission_point_not_transferred_from_permit() {
        String emissionPointId = "1";
        String emissionSourceStreamId = UUID.randomUUID().toString();
        String sourceStreamId = UUID.randomUUID().toString();

        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .emissionPoint(emissionPointId)
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceStreamId))
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_2)
            .biomassPercentages(BiomassPercentages.builder().contains(false).build())
            .build();
        MeasurementOfN2OEmissions measurementOfN2OEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_N2O, measurementOfN2OEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .measurementN2OEmissionPointParamMonitoringTiers(Map.of(emissionPointId,
                    MeasurementOfN2OMeasuredEmissionsTier.TIER_2))
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }
}