package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSkipReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSkipReviewActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSkipReviewDecision;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup.ADDITIONAL_INFORMATION;

class AerReviewServiceTest {

    private AerReviewService service = new AerReviewService();

    @Test
    void saveReviewGroupDecision() {
        final AerApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AerApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewSectionsCompleted(Map.of("entry", true))
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .request(Request.builder().type(RequestType.AER).build())
            .build();

        final UUID file = UUID.randomUUID();

        final AerDataReviewDecision decision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ChangesRequiredDecisionDetails.builder()
                .notes("Notes")
                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired",
                    Set.of(file))))
                .build())
            .build();

        final AerSaveReviewGroupDecisionRequestTaskActionPayload payload =
            AerSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .group(AerReviewGroup.INSTALLATION_DETAILS)
                .decision(decision)
                .build();

        // Invoke
        service.saveReviewGroupDecision(payload, requestTask);

        // Verify

        assertThat(requestTask.getPayload()).isInstanceOf(AerApplicationReviewRequestTaskPayload.class);

        final AerApplicationReviewRequestTaskPayload payloadSaved =
            (AerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getReviewGroupDecisions()).containsEntry(AerReviewGroup.INSTALLATION_DETAILS, decision);
        assertThat(payloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(payload.getReviewSectionsCompleted());
    }

    @Test
    void savePermitDecisionNotification() {
        final AppUser user = AppUser.builder().userId("user").build();
        final UUID file = UUID.randomUUID();

        final AerDataReviewDecision decision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ChangesRequiredDecisionDetails.builder()
                .notes("Notes")
                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired",
                    Set.of(file))))
                .build())
            .build();
        final AerApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AerApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewGroupDecisions(Map.of(ADDITIONAL_INFORMATION, decision))
                .reviewAttachments(Map.of(file, "fileName"))
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .request(Request.builder().type(RequestType.AER).payload(AerRequestPayload.builder().build()).build())
            .build();

        // Invoke
        service.updateRequestPayload(requestTask, user);

        // Verify
        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(AerRequestPayload.class);

        final AerRequestPayload payloadSaved = (AerRequestPayload) requestTask.getRequest().getPayload();
        assertThat(payloadSaved.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(payloadSaved.getReviewGroupDecisions()).containsEntry(ADDITIONAL_INFORMATION,
            decision);
        assertThat(payloadSaved.getReviewAttachments()).isEqualTo(Map.of(file, "fileName"));
    }

    @Test
    void savePermitDecisionNotification_noReviewGroupDecisions() {
        final AppUser user = AppUser.builder().userId("user").build();
        final UUID file = UUID.randomUUID();

        final AerApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AerApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewAttachments(Map.of(file, "fileName"))
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .request(Request.builder().type(RequestType.AER).payload(AerRequestPayload.builder().build()).build())
            .build();

        service.updateRequestPayload(requestTask, user);

        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(AerRequestPayload.class);

        final AerRequestPayload payloadSaved = (AerRequestPayload) requestTask.getRequest().getPayload();
        assertThat(payloadSaved.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(payloadSaved.getReviewGroupDecisions()).isEmpty();
        assertThat(payloadSaved.getReviewAttachments()).isEqualTo(Map.of(file, "fileName"));
    }

    @Test
    void savePermitDecisionNotification_noReviewAttachments() {
        final AppUser user = AppUser.builder().userId("user").build();

        final AerDataReviewDecision decision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ChangesRequiredDecisionDetails.builder()
                .notes("Notes")
                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired",
                    Set.of(UUID.randomUUID()))))
                .build())
            .build();
        final AerApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AerApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewGroupDecisions(Map.of(ADDITIONAL_INFORMATION, decision))
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .request(Request.builder().type(RequestType.AER).payload(AerRequestPayload.builder().build()).build())
            .build();

        service.updateRequestPayload(requestTask, user);

        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(AerRequestPayload.class);

        final AerRequestPayload payloadSaved = (AerRequestPayload) requestTask.getRequest().getPayload();
        assertThat(payloadSaved.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(payloadSaved.getReviewGroupDecisions()).containsEntry(ADDITIONAL_INFORMATION,
            decision);
        assertThat(payloadSaved.getReviewAttachments()).isEmpty();
    }

    @Test
    void saveRequestReturnForAmends() {
        final AppUser user = AppUser.builder().userId("user").build();

        final AerDataReviewDecision decision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ChangesRequiredDecisionDetails.builder()
                .notes("Notes")
                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired",
                    Set.of(UUID.randomUUID()))))
                .build())
            .build();
        final AerApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AerApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewGroupDecisions(Map.of(ADDITIONAL_INFORMATION, decision))
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .request(Request.builder().type(RequestType.AER).payload(AerRequestPayload.builder().build()).build())
            .build();

        service.saveRequestReturnForAmends(requestTask, user);

        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(AerRequestPayload.class);
        AerRequestPayload aerRequestPayload = (AerRequestPayload) requestTask.getRequest().getPayload();
        assertEquals(aerRequestPayload.getRegulatorReviewer(), user.getUserId());
        assertEquals(aerRequestPayload.getReviewGroupDecisions(), reviewRequestTaskPayload.getReviewGroupDecisions());
        assertEquals(aerRequestPayload.getReviewAttachments(), reviewRequestTaskPayload.getReviewAttachments());
        assertEquals(aerRequestPayload.getReviewSectionsCompleted(),
            reviewRequestTaskPayload.getReviewSectionsCompleted());
    }

    @Test
    void saveAmendOfAer() {
        AerSaveApplicationAmendRequestTaskActionPayload aerSaveApplicationAmendRequestTaskActionPayload =
            AerSaveApplicationAmendRequestTaskActionPayload.builder()
                .aer(Aer.builder()
                    .abbreviations(Abbreviations.builder().build())
                    .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                        .monitoringApproachEmissions(
                            Map.of(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2Emissions.builder().build())
                        )
                        .build())
                    .build())
                .aerSectionsCompleted(Map.of("section", List.of(true)))
                .reviewSectionsCompleted(Map.of("reviewSection", true))
                .build();

        AerApplicationAmendsSubmitRequestTaskPayload aerApplicationAmendsSubmitRequestTaskPayload =
            AerApplicationAmendsSubmitRequestTaskPayload.builder()
                .aer(Aer.builder()
                    .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                        .monitoringApproachEmissions(
                            Map.of(MonitoringApproachType.CALCULATION_PFC, CalculationOfPfcEmissions.builder().build())
                        )
                        .build())
                    .build())
                .reviewGroupDecisions(new HashMap<>(Map.of(
                    AerReviewGroup.CALCULATION_PFC, AerDataReviewDecision.builder().build()
                )))
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(aerApplicationAmendsSubmitRequestTaskPayload)
            .build();

        service.saveAmendOfAer(aerSaveApplicationAmendRequestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(AerApplicationAmendsSubmitRequestTaskPayload.class);
        AerApplicationAmendsSubmitRequestTaskPayload aerApplicationAmendsSubmitRequestTaskPayloadSaved =
            (AerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();
        assertEquals(aerApplicationAmendsSubmitRequestTaskPayloadSaved.getAerSectionsCompleted(),
            aerSaveApplicationAmendRequestTaskActionPayload.getAerSectionsCompleted());
        assertEquals(aerApplicationAmendsSubmitRequestTaskPayloadSaved.getReviewSectionsCompleted(),
            aerSaveApplicationAmendRequestTaskActionPayload.getReviewSectionsCompleted());
        assertEquals(aerApplicationAmendsSubmitRequestTaskPayloadSaved.getAer(),
            aerSaveApplicationAmendRequestTaskActionPayload.getAer());
        assertFalse(aerApplicationAmendsSubmitRequestTaskPayloadSaved.isVerificationPerformed());
    }

    @Test
    void updateRequestPayloadWithSkipReviewOutcome() {

        String userId = "userId";
        AppUser user = AppUser.builder().userId(userId).build();

        AerRequestPayload requestPayload = AerRequestPayload
                .builder()
                .aer(Aer.builder().monitoringApproachEmissions(MonitoringApproachEmissions.builder().build()).build())
                .verificationReport(AerVerificationReport.builder().build())
                .verificationPerformed(true)
                .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .build();

        AerApplicationSkipReviewRequestTaskActionPayload skipReviewRequestTaskPayload = AerApplicationSkipReviewRequestTaskActionPayload.builder()
                .aerSkipReviewDecision(AerSkipReviewDecision.builder().type(AerSkipReviewActionType.OTHER).reason("Test").build())
                .build();

        service.updateRequestPayloadWithSkipReviewOutcome(requestTask, skipReviewRequestTaskPayload, user);

        AerRequestPayload updatedRequestPayload = (AerRequestPayload) request.getPayload();

        Assertions.assertTrue(updatedRequestPayload.getReviewGroupDecisions().values().stream().filter(
                dec -> dec instanceof AerDataReviewDecision
        ).allMatch(dec -> ((AerDataReviewDecision) dec).getType() == AerDataReviewDecisionType.ACCEPTED));

        Assertions.assertTrue(updatedRequestPayload.getReviewGroupDecisions().values().stream().filter(
                dec -> dec instanceof AerVerificationReportDataReviewDecision
        ).allMatch(dec -> ((AerVerificationReportDataReviewDecision) dec).getType() == AerVerificationReportDataReviewDecisionType.ACCEPTED));
    }
}
