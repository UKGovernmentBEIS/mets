package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGaps;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaFuelDensityType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaFuelUseMonitoringDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAviationAerCorsiaReviewServiceTest {

    @InjectMocks
    private RequestAviationAerCorsiaReviewService reviewService;

    @Mock
    private AviationAerTradingSchemeValidatorService<AviationAerCorsiaContainer> aviationAerCorsiaValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AviationReportableEmissionsService aviationReportableEmissionsService;

    @Mock
    private AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper;

    @Test
    void saveReviewGroupDecision() {
        AerDataReviewDecision acceptedDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_PAYLOAD)
                .reviewGroupDecisions(new HashMap<>(Map.of(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, acceptedDecision)))
                .reviewSectionsCompleted(Map.of("section_a", true))
                .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .build();

        AerDataReviewDecision withdrawnDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder()
                .requiredChanges(List.of(ReviewDecisionRequiredChange.builder().reason("reason").build()))
                .notes("decision notes")
                .build())
            .build();

        AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload =
            AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .group(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS)
                .decision(withdrawnDecision)
                .reviewSectionsCompleted(Map.of("section_a", true, "section_b", true))
                .build();

        // invoke
        reviewService.saveReviewGroupDecision(taskActionPayload, requestTask);

        // verify
        assertThat(requestTask.getPayload()).isInstanceOf(AviationAerCorsiaApplicationReviewRequestTaskPayload.class);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayloadSaved =
            (AviationAerCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(reviewRequestTaskPayloadSaved.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
            AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, acceptedDecision,
            AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, withdrawnDecision));
        assertThat(reviewRequestTaskPayloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(taskActionPayload.getReviewSectionsCompleted());
    }

    @Test
    void updateRequestPayloadWithReviewOutcome() {
        String userId = "userId";
        PmrvUser user = PmrvUser.builder().userId(userId).build();

        AerDataReviewDecision acceptedDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = Map.of(
            AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, acceptedDecision,
            AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, acceptedDecision
        );
        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "seviceContactDetails", true,
            "operatorDetails", true);
        Map<UUID, String> reviewAttachments = Map.of(UUID.randomUUID(), "attachment1");
        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_PAYLOAD)
                .reviewGroupDecisions(reviewGroupDecisions)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .reviewAttachments(reviewAttachments)
                .build();

        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .request(request)
            .build();

        //invoke
        reviewService.updateRequestPayloadWithReviewOutcome(requestTask, user);

        //verify
        AviationAerCorsiaRequestPayload updatedRequestPayload =
            (AviationAerCorsiaRequestPayload) request.getPayload();

        assertEquals(userId, updatedRequestPayload.getRegulatorReviewer());
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
    }

    @Test
    void updateRequestPayloadWithSkipReviewOutcome() {

        String userId = "userId";
        PmrvUser user = PmrvUser.builder().userId(userId).build();

        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload
            .builder()
            .reportingRequired(true)
            .aer(AviationAerCorsia.builder().monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build()).build())
            .verificationReport(AviationAerCorsiaVerificationReport.builder().build())
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .build();

        reviewService.updateRequestPayloadWithSkipReviewOutcome(requestTask, user);

        AviationAerCorsiaRequestPayload updatedRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        Assertions.assertTrue(updatedRequestPayload.getReviewGroupDecisions().values().stream().filter(
            dec -> dec instanceof AerDataReviewDecision
        ).allMatch(dec -> ((AerDataReviewDecision) dec).getType() == AerDataReviewDecisionType.ACCEPTED));

        Assertions.assertTrue(updatedRequestPayload.getReviewGroupDecisions().values().stream().filter(
            dec -> dec instanceof AerVerificationReportDataReviewDecision
        ).allMatch(dec -> ((AerVerificationReportDataReviewDecision) dec).getType() == AerVerificationReportDataReviewDecisionType.ACCEPTED));
    }

    @Test
    void saveAerAmend() {
        AviationAerCorsia aer = AviationAerCorsia.builder()
                .additionalDocuments(EmpAdditionalDocuments.builder()
                        .exist(true)
                        .documents(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of("additionalDocuments", List.of(true));
        Map<String, Boolean> reviewSectionsCompleted = Map.of("additionalDocuments", false);
        AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload taskActionPayload =
                AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AVIATION_AER_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD)
                        .reportingRequired(true)
                        .aer(aer)
                        .aerSectionsCompleted(aerSectionsCompleted)
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                        .build();

        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                        .reportingRequired(false)
                        .reportingObligationDetails(AviationAerReportingObligationDetails.builder().build())
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(requestTaskPayload)
                .build();

        //invoke
        reviewService.saveAerAmend(taskActionPayload, requestTask);

        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload updatedRequestTaskPayload =
                (AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        assertTrue(updatedRequestTaskPayload.getReportingRequired());
        assertNull(updatedRequestTaskPayload.getReportingObligationDetails());
        assertEquals(aer, updatedRequestTaskPayload.getAer());

        assertThat(updatedRequestTaskPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);

        assertFalse(updatedRequestTaskPayload.isVerificationPerformed());
    }

    @Test
    void sendAmendedAerToVerifier() {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
            "Verification Section 1", List.of(true),
            "Verification Section 2", List.of(true)
        );
        AviationAerApplicationRequestVerificationRequestTaskActionPayload requestVerificationRequestTaskActionPayload =
            AviationAerApplicationRequestVerificationRequestTaskActionPayload.builder()
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .build();

        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AviationAerCorsiaReviewGroup.class);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_APPROACH, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.DATA_GAPS, aerDataReviewDecision);
        AviationAerCorsiaRequestPayload aviationAerCorsiaRequestPayload = AviationAerCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(AviationAerCorsia.builder()
                .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                    .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.STANDARD_DENSITY)
                        .build())
                    .build())
                .dataGaps(AviationAerCorsiaDataGaps.builder().build())
                .build())
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_CORSIA)
            .payload(aviationAerCorsiaRequestPayload)
            .accountId(accountId)
            .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerCorsia aviationAerCorsia = AviationAerCorsia.builder()
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                .build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of("Aer Section 1", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment 1");
        Map<UUID, String> reviewAttachments = Map.of(UUID.randomUUID(), "review attachment");
        Map<String, Boolean> reviewSectionsCompleted = Map.of("Review Section 1", true);

        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload =
            AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .reportingRequired(true)
                .verificationPerformed(true)
                .aer(aviationAerCorsia)
                .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                .aerSectionsCompleted(aerSectionsCompleted)
                .aerAttachments(aerAttachments)
                .reviewAttachments(reviewAttachments)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT)
            .request(request)
            .payload(aviationAerAmendsSubmitRequestTaskPayload)
            .build();

        AviationAerCorsiaContainer aerCorsiaContainer = AviationAerCorsiaContainer.builder().aer(aviationAerCorsia).build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerCorsiaApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
            AviationAerCorsiaApplicationSubmittedRequestActionPayload.builder()
                .reportingRequired(true)
                .aer(aviationAerCorsia)
                .build();

        when(aviationAerCorsiaReviewMapper.toAviationAerCorsiaContainer(aviationAerAmendsSubmitRequestTaskPayload, EmissionTradingScheme.CORSIA))
            .thenReturn(aerCorsiaContainer);
        doNothing().when(aviationAerCorsiaValidatorService).validateAer(aerCorsiaContainer);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerCorsiaReviewMapper
            .toAviationAerCorsiaApplicationSubmittedRequestActionPayload(aviationAerAmendsSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD))
            .thenReturn(submittedRequestActionPayload);

        reviewService.sendAmendedAerToVerifier(requestVerificationRequestTaskActionPayload, requestTask, pmrvUser);

        AviationAerCorsiaRequestPayload updatedRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        Assertions.assertTrue(updatedRequestPayload.getReportingRequired());
        assertNull(updatedRequestPayload.getReportingObligationDetails());
        Assertions.assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(aviationAerCorsia, updatedRequestPayload.getAer());
        assertThat(updatedRequestPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(updatedRequestPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestPayload.getVerificationSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(verificationSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);

        verify(aviationAerCorsiaReviewMapper, times(1))
            .toAviationAerCorsiaContainer(aviationAerAmendsSubmitRequestTaskPayload, EmissionTradingScheme.CORSIA);
        verify(aviationAerCorsiaValidatorService, times(1)).validateAer(aerCorsiaContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaReviewMapper, times(1))
            .toAviationAerCorsiaApplicationSubmittedRequestActionPayload(aviationAerAmendsSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER, pmrvUser.getUserId());
    }
}