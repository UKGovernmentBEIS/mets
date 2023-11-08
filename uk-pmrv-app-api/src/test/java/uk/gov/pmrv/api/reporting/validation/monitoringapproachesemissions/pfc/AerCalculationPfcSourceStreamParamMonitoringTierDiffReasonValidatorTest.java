package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.pfc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.ActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactorTier;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReasonType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PermitOriginatedCalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AerCalculationPfcSourceStreamParamMonitoringTierDiffReasonValidatorTest {

    private final AerCalculationPfcSourceStreamParamMonitoringTierDiffReasonValidator validator =
        new AerCalculationPfcSourceStreamParamMonitoringTierDiffReasonValidator();

    @Test
    @DisplayName("should validate correctly that both PFCEmissionFactorTier and ActivityDataTier are the same with " +
        "those of permit")
    void validate_valid() {
        String sourceStreamId = "1";
        String calculationPfcEmissionSourceStreamId = UUID.randomUUID().toString();
        ActivityDataTier activityDataTier = ActivityDataTier.TIER_2;
        PFCEmissionFactorTier pfcEmissionFactorTier = PFCEmissionFactorTier.TIER_2;

        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .id(calculationPfcEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTier(
                CalculationPfcParameterMonitoringTier.builder()
                    .activityDataTier(activityDataTier)
                    .emissionFactorTier(pfcEmissionFactorTier)
                    .build()
            )
            .build();
        CalculationOfPfcEmissions calculationOfPfcEmissions = CalculationOfPfcEmissions.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .sourceStreamEmissions(List.of(pfcSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_PFC, calculationOfPfcEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationPfcSourceStreamParamMonitoringTiers(
                    Map.of(calculationPfcEmissionSourceStreamId, PermitOriginatedCalculationPfcParameterMonitoringTier.builder()
                        .activityDataTier(activityDataTier)
                        .emissionFactorTier(pfcEmissionFactorTier)
                        .massBalanceApproachUsed(false)
                        .build()
                    )
                )
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_valid_when_source_stream_emission_id_empty() {
        String sourceStreamId = "1";
        ActivityDataTier activityDataTier = ActivityDataTier.TIER_2;
        PFCEmissionFactorTier pfcEmissionFactorTier = PFCEmissionFactorTier.TIER_2;

        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .sourceStream(sourceStreamId)
            .parameterMonitoringTier(
                CalculationPfcParameterMonitoringTier.builder()
                    .activityDataTier(activityDataTier)
                    .emissionFactorTier(pfcEmissionFactorTier)
                    .build()
            )
            .build();

        AerContainer aerContainer = AerContainer.builder().build();

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    @DisplayName("should emit AerViolation when the tiers are the same between AER and Permit but exists a diff reason")
    void validate_diff_reason_should_not_exist() {
        String sourceStreamId = "1";
        String calculationPfcEmissionSourceStreamId = UUID.randomUUID().toString();
        ActivityDataTier activityDataTier = ActivityDataTier.TIER_2;
        PFCEmissionFactorTier pfcEmissionFactorTier = PFCEmissionFactorTier.TIER_2;

        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .id(calculationPfcEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTier(
                CalculationPfcParameterMonitoringTier.builder()
                    .emissionFactorTier(pfcEmissionFactorTier)
                    .activityDataTier(activityDataTier)
                    .build()
            )
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .reason("some reason")
                .build())
            .build();
        CalculationOfPfcEmissions calculationOfPfcEmissions = CalculationOfPfcEmissions.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .sourceStreamEmissions(List.of(pfcSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_PFC, calculationOfPfcEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationPfcSourceStreamParamMonitoringTiers(
                    Map.of(calculationPfcEmissionSourceStreamId, PermitOriginatedCalculationPfcParameterMonitoringTier.builder()
                        .activityDataTier(activityDataTier)
                        .emissionFactorTier(pfcEmissionFactorTier)
                        .build())
                )
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission, aerContainer);
        AerViolation violation = aerViolations.get(0);
        assertEquals(CalculationOfPfcEmissions.class.getSimpleName(), violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON.getMessage(),
            violation.getMessage());
    }

    @Test
    @DisplayName("should emit AerViolation when the AER and Permit tiers are different but no reason exists")
    void validate_diff_reason_should_exist() {
        String sourceStreamId = "1";
        String calculationPfcEmissionSourceStreamId = UUID.randomUUID().toString();
        ActivityDataTier activityDataTier = ActivityDataTier.TIER_2;
        PFCEmissionFactorTier pfcEmissionFactorTier = PFCEmissionFactorTier.TIER_2;

        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .id(calculationPfcEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTier(
                CalculationPfcParameterMonitoringTier.builder().activityDataTier(activityDataTier).emissionFactorTier(pfcEmissionFactorTier).build())
            .build();
        CalculationOfPfcEmissions calculationOfPfcEmissions = CalculationOfPfcEmissions.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .sourceStreamEmissions(List.of(pfcSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_PFC, calculationOfPfcEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationPfcSourceStreamParamMonitoringTiers(
                    Map.of(calculationPfcEmissionSourceStreamId, PermitOriginatedCalculationPfcParameterMonitoringTier.builder()
                        .emissionFactorTier(pfcEmissionFactorTier)
                        .activityDataTier(ActivityDataTier.TIER_1)
                        .build())
                )
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission, aerContainer);
        AerViolation violation = aerViolations.get(0);
        assertEquals(CalculationOfPfcEmissions.class.getSimpleName(), violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON.getMessage(),
            violation.getMessage());
    }

    @Test
    @DisplayName("should emit AerViolation when AER and Permit tiers are different but the diff reason type is OTHER " +
        "and notifications exist")
    void validate_diff_reason_notifications_should_not_exist() {
        String sourceStreamId = "1";
        String calculationPfcEmissionSourceStreamId = UUID.randomUUID().toString();
        ActivityDataTier activityDataTier = ActivityDataTier.TIER_2;
        PFCEmissionFactorTier pfcEmissionFactorTier = PFCEmissionFactorTier.TIER_2;

        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .id(calculationPfcEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTier(CalculationPfcParameterMonitoringTier.builder()
                .activityDataTier(activityDataTier)
                .emissionFactorTier(pfcEmissionFactorTier)
                .build())
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .reason("reason")
                .type(ParameterMonitoringTierDiffReasonType.OTHER)
                .relatedNotifications(List.of("AEM256"))
                .build())
            .build();
        CalculationOfPfcEmissions calculationOfPfcEmissions = CalculationOfPfcEmissions.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .sourceStreamEmissions(List.of(pfcSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_PFC, calculationOfPfcEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationPfcSourceStreamParamMonitoringTiers(
                    Map.of(calculationPfcEmissionSourceStreamId, PermitOriginatedCalculationPfcParameterMonitoringTier.builder()
                        .emissionFactorTier(pfcEmissionFactorTier)
                        .activityDataTier(ActivityDataTier.TIER_1)
                        .build())
                )
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission, aerContainer);
        AerViolation violation = aerViolations.get(0);
        assertEquals(CalculationOfPfcEmissions.class.getSimpleName(), violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS_SHOULD_NOT_EXIST.getMessage(),
            violation.getMessage());
    }

    @Test
    @DisplayName("should emit AerViolation when AER and Permit tiers are different and the diff reason type is " +
        "DATA_GAP but notifications exist")
    void validate_diff_reason_invalid_notifications() {
        String sourceStreamId = "1";
        String calculationPfcEmissionSourceStreamId = UUID.randomUUID().toString();
        ActivityDataTier activityDataTier = ActivityDataTier.TIER_2;
        PFCEmissionFactorTier pfcEmissionFactorTier = PFCEmissionFactorTier.TIER_2;

        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .id(calculationPfcEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTier(CalculationPfcParameterMonitoringTier.builder()
                .activityDataTier(activityDataTier)
                .emissionFactorTier(pfcEmissionFactorTier)
                .build())
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .type(ParameterMonitoringTierDiffReasonType.DATA_GAP)
                .reason("some reason")
                .relatedNotifications(List.of("AEM256"))
                .build())
            .build();
        CalculationOfPfcEmissions calculationOfPfcEmissions = CalculationOfPfcEmissions.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .sourceStreamEmissions(List.of(pfcSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_PFC, calculationOfPfcEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationPfcSourceStreamParamMonitoringTiers(
                    Map.of(calculationPfcEmissionSourceStreamId, PermitOriginatedCalculationPfcParameterMonitoringTier.builder()
                        .emissionFactorTier(pfcEmissionFactorTier)
                        .activityDataTier(ActivityDataTier.TIER_1)
                        .build())
                )
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission, aerContainer);
        AerViolation violation = aerViolations.get(0);
        assertEquals(CalculationOfPfcEmissions.class.getSimpleName(), violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS.getMessage(),
            violation.getMessage());
    }

    @Test
    void validate_diff_reason_with_empty_notifications() {
        String sourceStreamId = "1";
        String calculationPfcEmissionSourceStreamId = UUID.randomUUID().toString();
        ActivityDataTier activityDataTier = ActivityDataTier.TIER_2;
        PFCEmissionFactorTier pfcEmissionFactorTier = PFCEmissionFactorTier.TIER_2;

        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .id(calculationPfcEmissionSourceStreamId)
            .sourceStream(sourceStreamId)
            .parameterMonitoringTier(CalculationPfcParameterMonitoringTier.builder()
                .activityDataTier(activityDataTier)
                .emissionFactorTier(pfcEmissionFactorTier)
                .build())
            .parameterMonitoringTierDiffReason(ParameterMonitoringTierDiffReason.builder()
                .type(ParameterMonitoringTierDiffReasonType.DATA_GAP)
                .reason("some reason")
                .relatedNotifications(Collections.emptyList())
                .build())
            .build();
        CalculationOfPfcEmissions calculationOfPfcEmissions = CalculationOfPfcEmissions.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .sourceStreamEmissions(List.of(pfcSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_PFC, calculationOfPfcEmissions))
            .build();

        Aer aer = Aer.builder().monitoringApproachEmissions(monitoringApproachEmissions).build();
        MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers =
            MonitoringApproachMonitoringTiers.builder()
                .calculationPfcSourceStreamParamMonitoringTiers(
                    Map.of(calculationPfcEmissionSourceStreamId, PermitOriginatedCalculationPfcParameterMonitoringTier.builder()
                        .emissionFactorTier(pfcEmissionFactorTier)
                        .activityDataTier(ActivityDataTier.TIER_1)
                        .build())
                )
                .build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(permitMonitoringApproachMonitoringTiers)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).permitOriginatedData(permitOriginatedData).build();

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission, aerContainer);
        assertEquals(0, aerViolations.size());
    }
}