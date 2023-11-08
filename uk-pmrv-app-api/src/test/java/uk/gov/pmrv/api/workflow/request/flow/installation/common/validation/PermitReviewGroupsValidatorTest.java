package uk.gov.pmrv.api.workflow.request.flow.installation.common.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitReviewGroupsValidatorTest {

    @InjectMocks
    private PermitReviewGroupsValidator<PermitIssuanceReviewDecision> validator;

    @Mock
    private PermitReviewService permitReviewService;

    @Test
    void containsDecisionForAllPermitGroups() {
        Permit permit = Permit.builder()
            .abbreviations(Abbreviations.builder().build())
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(Map.of(
                    MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2MonitoringApproach.builder().build()
                ))
                .build())
            .build();
        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permit(permit)
                .reviewGroupDecisions(Map.of(
                        PermitReviewGroup.CONFIDENTIALITY_STATEMENT, buildReviewDecision(),
                        PermitReviewGroup.FUELS_AND_EQUIPMENT, buildReviewDecision(),
                        PermitReviewGroup.INSTALLATION_DETAILS, buildReviewDecision(),
                        PermitReviewGroup.MANAGEMENT_PROCEDURES, buildReviewDecision(),
                        PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, buildReviewDecision(),
                        PermitReviewGroup.ADDITIONAL_INFORMATION, buildReviewDecision(),
                        PermitReviewGroup.DEFINE_MONITORING_APPROACHES, buildReviewDecision(),
                        PermitReviewGroup.UNCERTAINTY_ANALYSIS, buildReviewDecision(),
                        PermitReviewGroup.PERMIT_TYPE, buildReviewDecision(),
                        PermitReviewGroup.MEASUREMENT_CO2, buildReviewDecision()
                ))
                .build();
        
        // Invoke
        boolean result = validator.containsDecisionForAllPermitGroups(taskPayload);

        // Verify
        assertTrue(result);
    }

    @Test
    void containsDecisionForAllPermitGroups_not_valid() {
        Permit permit = Permit.builder()
            .abbreviations(Abbreviations.builder().build())
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(Map.of(
                    MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2MonitoringApproach.builder().build()
                ))
                .build())
            .build();
        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
            .permit(permit)
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.CALCULATION_CO2, buildReviewDecision()
            ))
            .build();

        // Invoke
        boolean result = validator.containsDecisionForAllPermitGroups(taskPayload);

        // Verify
        assertFalse(result);
    }

    @Test
    void containsAmendNeededGroups_false() {
        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
            .permit(buildPermit())
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.CONFIDENTIALITY_STATEMENT,
                PermitIssuanceReviewDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .details(ReviewDecisionDetails.builder().notes("notes").build())
                    .build()
            ))
            .build();

        // Invoke
        boolean result = validator.containsAmendNeededGroups(taskPayload);

        // Verify
        assertFalse(result);
    }

    @Test
    void containsAmendNeededGroups_true() {
        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
            .permit(buildPermit())
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.CONFIDENTIALITY_STATEMENT,
                PermitIssuanceReviewDecision.builder()
                    .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .details(ChangesRequiredDecisionDetails.builder().notes("notes")
                        .requiredChanges(List.of(new ReviewDecisionRequiredChange("change", Collections.emptySet()))).build())
                    .build()
            ))
            .build();

        // Invoke
        boolean result = validator.containsAmendNeededGroups(taskPayload);

        // Verify
        assertTrue(result);
    }

    private PermitIssuanceReviewDecision buildReviewDecision() {
        return PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
    }

    private Permit buildPermit() {
        return Permit.builder()
            .monitoringApproaches(buildMonitoringApproaches())
            .build();
    }

    private MonitoringApproaches buildMonitoringApproaches() {
        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches = new EnumMap<>(MonitoringApproachType.class);

        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2MonitoringApproach.builder().build());

        return MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build();
    }

}
