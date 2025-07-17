package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerificationDecisionType;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerifiedSatisfactoryDecision;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGaps;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerFuelMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerSmallEmittersMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service.AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAviationAerUkEtsReviewServiceTest {

    @InjectMocks
    private RequestAviationAerUkEtsReviewService reviewService;

    @Mock
    private AviationAerTradingSchemeValidatorService<AviationAerUkEtsContainer> aviationAerUkEtsValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AviationReportableEmissionsService aviationReportableEmissionsService;

    @Mock
    private AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;

    @Mock
    private AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService syncAerAttachmentsService;
    
    @Mock
    private AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper;

    @Test
    void saveReviewGroupDecision() {
        AerDataReviewDecision acceptedDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
            .type(AerDataReviewDecisionType. ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD)
                .reviewGroupDecisions(new HashMap<>(Map.of(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, acceptedDecision)))
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

        AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload =
            AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.builder()
            .group(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS)
            .decision(withdrawnDecision)
            .reviewSectionsCompleted(Map.of("section_a", true, "section_b", true))
            .build();

        // invoke
        reviewService.saveReviewGroupDecision(taskActionPayload, requestTask);

        // verify
        assertThat(requestTask.getPayload()).isInstanceOf(AviationAerUkEtsApplicationReviewRequestTaskPayload.class);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayloadSaved =
            (AviationAerUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(reviewRequestTaskPayloadSaved.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
            AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, acceptedDecision,
            AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, withdrawnDecision));
        assertThat(reviewRequestTaskPayloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(taskActionPayload.getReviewSectionsCompleted());
    }

    @Test
    void updateRequestPayloadWithReviewOutcome() {
        String userId = "userId";
        AppUser user = AppUser.builder().userId(userId).build();

        AerDataReviewDecision acceptedDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
            .type(AerDataReviewDecisionType. ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = Map.of(
            AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, acceptedDecision,
            AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, acceptedDecision
        );
        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "seviceContactDetails", true,
            "operatorDetails", true);
        Map<UUID, String> reviewAttachments = Map.of(UUID.randomUUID(), "attachment1");
        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD)
                .reviewGroupDecisions(reviewGroupDecisions)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .reviewAttachments(reviewAttachments)
                .build();

        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .payload(reviewRequestTaskPayload)
            .request(request)
            .build();

        //invoke
        reviewService.updateRequestPayloadWithReviewOutcome(requestTask, user);

        //verify
        AviationAerUkEtsRequestPayload updatedRequestPayload =
            (AviationAerUkEtsRequestPayload) request.getPayload();

        assertEquals(userId, updatedRequestPayload.getRegulatorReviewer());
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
    }
    
    @Test
    void updateRequestPayloadWithSkipReviewOutcome() {
        
        String userId = "userId";
        AppUser user = AppUser.builder().userId(userId).build();

        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload
            .builder()
            .reportingRequired(true)
            .aer(AviationAerUkEts.builder().monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING).build()).build())
            .verificationReport(AviationAerUkEtsVerificationReport.builder().safExists(true).build())
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .build();

        reviewService.updateRequestPayloadWithSkipReviewOutcome(requestTask, user);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        Assertions.assertTrue(updatedRequestPayload.getReviewGroupDecisions().values().stream().filter(
            dec -> dec instanceof AerDataReviewDecision
        ).allMatch(dec -> ((AerDataReviewDecision) dec).getType() == AerDataReviewDecisionType.ACCEPTED));

        Assertions.assertTrue(updatedRequestPayload.getReviewGroupDecisions().values().stream().filter(
            dec -> dec instanceof AerVerificationReportDataReviewDecision
        ).allMatch(dec -> ((AerVerificationReportDataReviewDecision) dec).getType() == AerVerificationReportDataReviewDecisionType.ACCEPTED));
    }

    @Test
    void saveAerAmend() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .additionalDocuments(EmpAdditionalDocuments.builder()
                .exist(true)
                .documents(Set.of(UUID.randomUUID()))
                .build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of("additionalDocuments", List.of(true));
        Map<String, Boolean> reviewSectionsCompleted = Map.of("additionalDocuments", false);
        AviationAerUkEtsSaveApplicationAmendRequestTaskActionPayload taskActionPayload =
            AviationAerUkEtsSaveApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_UKETS_SAVE_APPLICATION_AMEND_PAYLOAD)
                .reportingRequired(true)
                .aer(aer)
                .aerSectionsCompleted(aerSectionsCompleted)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
            AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .reportingRequired(false)
                .reportingObligationDetails(AviationAerReportingObligationDetails.builder().build())
                .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        //invoke
        reviewService.saveAerAmend(taskActionPayload, requestTask);

        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload updatedRequestTaskPayload =
            (AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        assertTrue(updatedRequestTaskPayload.getReportingRequired());
        assertNull(updatedRequestTaskPayload.getReportingObligationDetails());
        assertEquals(aer, updatedRequestTaskPayload.getAer());

        assertThat(updatedRequestTaskPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);

        assertFalse(updatedRequestTaskPayload.isVerificationPerformed());
        
        verify(syncAerAttachmentsService, times(1)).sync(taskActionPayload.getReportingRequired(), requestTaskPayload);
    }

    @Test
    void sendAmendedAerToVerifier() {
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId("userId").build();
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
            "Verification Section 1", List.of(true),
            "Verification Section 2", List.of(true)
            );
        Map<String, Boolean> requestTaskActionReviewSectionsCompleted = Map.of(
                "Review Section 1", true,
                "Review Section 2", true
                );
        AviationAerApplicationRequestVerificationRequestTaskActionPayload requestVerificationRequestTaskActionPayload =
            AviationAerApplicationRequestVerificationRequestTaskActionPayload.builder()
            .reviewSectionsCompleted(requestTaskActionReviewSectionsCompleted)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .build();

        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AviationAerUkEtsReviewGroup.class);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_APPROACH, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.DATA_GAPS, aerDataReviewDecision);
        AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(AviationAerUkEts.builder()
                .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                    .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                    .build())
                .dataGaps(AviationAerDataGaps.builder().build())
                .build())
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(aviationAerUkEtsRequestPayload)
            .accountId(accountId)
            .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerUkEts aviationAerUkEts = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerSmallEmittersMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .numOfFlightsJanApr(30L)
                .numOfFlightsMayAug(40L)
                .numOfFlightsSepDec(50L)
                .totalEmissions(BigDecimal.TEN)
                .build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of("Aer Section 1", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment 1");
        Map<UUID, String> reviewAttachments = Map.of(UUID.randomUUID(), "review attachment");
        Map<String, Boolean> requestTaskReviewSectionsCompleted = Map.of("Review Section 1", true);

        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .reportingRequired(true)
                .verificationPerformed(true)
                .aer(aviationAerUkEts)
                .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                .aerSectionsCompleted(aerSectionsCompleted)
                .aerAttachments(aerAttachments)
                .reviewAttachments(reviewAttachments)
                .reviewSectionsCompleted(requestTaskReviewSectionsCompleted)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT)
            .request(request)
            .payload(aviationAerAmendsSubmitRequestTaskPayload)
            .build();

        AviationAerUkEtsContainer aerUkEtsContainer = AviationAerUkEtsContainer.builder().reportingRequired(Boolean.TRUE).aer(aviationAerUkEts).build();
        AviationAerUkEtsSubmittedEmissions submittedEmissions = AviationAerUkEtsSubmittedEmissions.builder()
            .aviationAerTotalEmissions(AviationAerTotalEmissions.builder().totalEmissions(BigDecimal.TEN).build())
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerUkEtsApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
            AviationAerUkEtsApplicationSubmittedRequestActionPayload.builder()
                .reportingRequired(true)
                .aer(aviationAerUkEts)
                .build();

        when(aviationAerUkEtsReviewMapper.toAviationAerUkEtsContainer(aviationAerAmendsSubmitRequestTaskPayload, EmissionTradingScheme.UK_ETS_AVIATION))
            .thenReturn(aerUkEtsContainer);
        doNothing().when(aviationAerUkEtsValidatorService).validateAer(aerUkEtsContainer);
        when(aviationAerUkEtsEmissionsCalculationService.calculateSubmittedEmissions(aerUkEtsContainer)).thenReturn(submittedEmissions);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		when(aviationAerUkEtsReviewMapper.toAviationAerUkEtsApplicationSubmittedRequestActionPayload(
				aviationAerAmendsSubmitRequestTaskPayload, accountInfo, submittedEmissions,
				RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD))
				.thenReturn(submittedRequestActionPayload);

        reviewService.sendAmendedAerToVerifier(requestVerificationRequestTaskActionPayload, requestTask, appUser);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        Assertions.assertTrue(updatedRequestPayload.getReportingRequired());
        assertNull(updatedRequestPayload.getReportingObligationDetails());
        Assertions.assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(aviationAerUkEts, updatedRequestPayload.getAer());
        assertThat(updatedRequestPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(updatedRequestPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestPayload.getVerificationSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(verificationSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(requestTaskActionReviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsOnlyKeys(AviationAerUkEtsReviewGroup.MONITORING_APPROACH);
        assertEquals(submittedEmissions, updatedRequestPayload.getSubmittedEmissions());

        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsContainer(aviationAerAmendsSubmitRequestTaskPayload, EmissionTradingScheme.UK_ETS_AVIATION);
        verify(aviationAerUkEtsValidatorService, times(1)).validateAer(aerUkEtsContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerAmendsSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER, appUser.getUserId());
    }

    @Test
    void sendAmendedAerToRegulator_when_reporting_required_true() {
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId("userId").build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
            "Section 1", List.of(true),
            "Section 2", List.of(true)
        );
        
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
                "Verification Section 1", List.of(true),
                "Verification Section 2", List.of(true)
                );
        Map<String, Boolean> requestTaskActionReviewSectionsCompleted = Map.of(
                "Review Section 1", true,
                "Review Section 2", true
                );
            
        AviationAerSubmitApplicationAmendRequestTaskActionPayload submitApplicationAmendRequestTaskActionPayload =
            AviationAerSubmitApplicationAmendRequestTaskActionPayload.builder()
                .aerSectionsCompleted(aerSectionsCompleted)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .reviewSectionsCompleted(requestTaskActionReviewSectionsCompleted)
                .build();

        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
                .verificationData(AviationAerUkEtsVerificationData.builder()
                        .overallDecision(AviationAerVerifiedSatisfactoryDecision.builder()
                        		.type(AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY)
                        		.build())
                        .build())
                .build();
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AviationAerUkEtsReviewGroup.class);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_APPROACH, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.DATA_GAPS, aerDataReviewDecision);
        AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(AviationAerUkEts.builder()
                .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                    .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                    .build())
                .dataGaps(AviationAerDataGaps.builder().build())
                .build())
            .verificationReport(verificationReport)
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();
        AviationAerRequestMetadata aviationAerRequestMetadata = AviationAerRequestMetadata.builder().build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(aviationAerUkEtsRequestPayload)
            .accountId(accountId)
            .metadata(aviationAerRequestMetadata)
            .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerUkEts aviationAerUkEts = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerSmallEmittersMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .numOfFlightsJanApr(30L)
                .numOfFlightsMayAug(40L)
                .numOfFlightsSepDec(50L)
                .totalEmissions(BigDecimal.TEN)
                .build())
            .build();
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment 1");
        Map<UUID, String> reviewAttachments = Map.of(UUID.randomUUID(), "review attachment");
        Map<String, Boolean> requestTaskReviewSectionsCompleted = Map.of("Review Section 1", true);
        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .reportingRequired(true)
                .verificationPerformed(true)
                .aer(aviationAerUkEts)
                .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                .aerSectionsCompleted(Map.of("Aer Section 1", List.of(false)))
                .aerAttachments(aerAttachments)
                .reviewSectionsCompleted(requestTaskReviewSectionsCompleted)
                .reviewAttachments(reviewAttachments)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT)
            .request(request)
            .payload(aviationAerAmendsSubmitRequestTaskPayload)
            .build();

        AviationAerUkEtsContainer aerUkEtsContainer = AviationAerUkEtsContainer.builder()
        		.reportingRequired(Boolean.TRUE)
        		.aer(aviationAerUkEts)
        		.verificationReport(verificationReport)
        		.build();
        AviationAerUkEtsSubmittedEmissions submittedEmissions = AviationAerUkEtsSubmittedEmissions.builder()
            .aviationAerTotalEmissions(AviationAerTotalEmissions.builder().totalEmissions(BigDecimal.TEN).build())
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerUkEtsApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
            AviationAerUkEtsApplicationSubmittedRequestActionPayload.builder()
                .reportingRequired(true)
                .aer(aviationAerUkEts)
                .verificationReport(verificationReport)
                .build();
        AviationAerUkEtsTotalReportableEmissions totalEmissions = AviationAerUkEtsTotalReportableEmissions.builder()
                .reportableEmissions(BigDecimal.valueOf(200))
                .build();

        when(aviationAerUkEtsReviewMapper.toAviationAerUkEtsContainer(aviationAerAmendsSubmitRequestTaskPayload, verificationReport, EmissionTradingScheme.UK_ETS_AVIATION))
            .thenReturn(aerUkEtsContainer);
        doNothing().when(aviationAerUkEtsValidatorService).validate(aerUkEtsContainer);
        when(aviationAerUkEtsEmissionsCalculationService.calculateSubmittedEmissions(aerUkEtsContainer)).thenReturn(submittedEmissions);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerUkEtsReviewMapper
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerAmendsSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD))
            .thenReturn(submittedRequestActionPayload);
        when(aviationReportableEmissionsService.updateReportableEmissions(aerUkEtsContainer, accountId, false)).thenReturn(totalEmissions);

        reviewService.sendAmendedAerToRegulator(submitApplicationAmendRequestTaskActionPayload, requestTask, appUser);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        Assertions.assertTrue(updatedRequestPayload.getReportingRequired());
        assertNull(updatedRequestPayload.getReportingObligationDetails());
        Assertions.assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(aviationAerUkEts, updatedRequestPayload.getAer());
        assertEquals(updatedRequestPayload.getAerMonitoringPlanVersions(), aerMonitoringPlanVersions);
        assertThat(updatedRequestPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(updatedRequestPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(requestTaskActionReviewSectionsCompleted);
        assertThat(updatedRequestPayload.getVerificationSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(verificationSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsOnlyKeys(AviationAerUkEtsReviewGroup.MONITORING_APPROACH);
        assertEquals(submittedEmissions, updatedRequestPayload.getSubmittedEmissions());
        assertEquals(submittedRequestActionPayload.getVerificationReport(), updatedRequestPayload.getVerificationReport());

        AviationAerRequestMetadata updatedRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        assertEquals(totalEmissions.getReportableEmissions(), updatedRequestMetadata.getEmissions());
        assertEquals(AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY,
        		updatedRequestMetadata.getOverallAssessmentType());

        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsContainer(aviationAerAmendsSubmitRequestTaskPayload, verificationReport, EmissionTradingScheme.UK_ETS_AVIATION);
        verify(aviationAerUkEtsValidatorService, times(1)).validate(aerUkEtsContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerAmendsSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED, appUser.getUserId());
        verify(aviationReportableEmissionsService, times(1)).updateReportableEmissions(aerUkEtsContainer, accountId, false);

        verify(aviationReportableEmissionsService, never()).deleteReportableEmissions(any(), any());
    }

    @Test
    void sendAmendedAerToRegulator_when_reporting_required_false() {
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId("userId").build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
            "Section 1", List.of(true),
            "Section 2", List.of(true)
        );
        
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
                "Verification Section 1", List.of(true),
                "Verification Section 2", List.of(true)
                );
        Map<String, Boolean> requestTaskActionReviewSectionsCompleted = Map.of(
                "Review Section 1", true,
                "Review Section 2", true
                );
        
        AviationAerSubmitApplicationAmendRequestTaskActionPayload submitApplicationAmendRequestTaskActionPayload =
            AviationAerSubmitApplicationAmendRequestTaskActionPayload.builder()
                .aerSectionsCompleted(aerSectionsCompleted)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .reviewSectionsCompleted(requestTaskActionReviewSectionsCompleted)
                .build();

        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder().build();
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        AerVerificationReportDataReviewDecision verificationReportDataReviewDecision =
            AerVerificationReportDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
                .type(AerVerificationReportDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AviationAerUkEtsReviewGroup.class);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_APPROACH, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.DATA_GAPS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.VERIFIER_DETAILS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OVERALL_DECISION, verificationReportDataReviewDecision);
        AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(AviationAerUkEts.builder()
                .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                    .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                    .build())
                .dataGaps(AviationAerDataGaps.builder().build())
                .build())
            .verificationReport(verificationReport)
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();
        AviationAerRequestMetadata aviationAerRequestMetadata = AviationAerRequestMetadata.builder()
        		.emissions(BigDecimal.TEN)
        		.overallAssessmentType(AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY)
        		.build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(aviationAerUkEtsRequestPayload)
            .accountId(accountId)
            .metadata(aviationAerRequestMetadata)
            .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerReportingObligationDetails reportingObligationDetails = AviationAerReportingObligationDetails.builder()
            .noReportingReason("reason")
            .build();
        Map<UUID, String> reviewAttachments = Map.of(UUID.randomUUID(), "review attachment");
        Map<String, Boolean> reviewSectionsCompleted = Map.of("Review Section 1", true);
        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .reportingRequired(false)
                .reportingObligationDetails(reportingObligationDetails)
                .verificationPerformed(false)
                .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                .aerSectionsCompleted(Map.of("Aer Section 1", List.of(false)))
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .reviewAttachments(reviewAttachments)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT)
            .request(request)
            .payload(aviationAerAmendsSubmitRequestTaskPayload)
            .build();

        AviationAerUkEtsContainer aerUkEtsContainer = AviationAerUkEtsContainer.builder()
            .reportingRequired(Boolean.FALSE)
            .reportingObligationDetails(reportingObligationDetails)
            .reportingYear(Year.of(2022))
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerUkEtsApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
            AviationAerUkEtsApplicationSubmittedRequestActionPayload.builder()
                .reportingRequired(false)
                .reportingObligationDetails(reportingObligationDetails)
                .build();

        when(aviationAerUkEtsReviewMapper.toAviationAerUkEtsContainer(aviationAerAmendsSubmitRequestTaskPayload, null, EmissionTradingScheme.UK_ETS_AVIATION))
            .thenReturn(aerUkEtsContainer);
        doNothing().when(aviationAerUkEtsValidatorService).validate(aerUkEtsContainer);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerUkEtsReviewMapper
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerAmendsSubmitRequestTaskPayload, accountInfo, null, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD))
            .thenReturn(submittedRequestActionPayload);

        reviewService.sendAmendedAerToRegulator(submitApplicationAmendRequestTaskActionPayload, requestTask, appUser);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        Assertions.assertFalse(updatedRequestPayload.getReportingRequired());
        assertEquals(reportingObligationDetails, updatedRequestPayload.getReportingObligationDetails());
        assertNull(updatedRequestPayload.getAer());
        Assertions.assertFalse(updatedRequestPayload.isVerificationPerformed());
        assertEquals(updatedRequestPayload.getAerMonitoringPlanVersions(), aerMonitoringPlanVersions);
        assertThat(updatedRequestPayload.getAerAttachments()).isEmpty();
        assertThat(updatedRequestPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(requestTaskActionReviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).isEmpty();
        assertNull(updatedRequestPayload.getSubmittedEmissions());

        AviationAerRequestMetadata updatedRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        assertNull(updatedRequestMetadata.getEmissions());
        assertNull(updatedRequestMetadata.getOverallAssessmentType());

        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsContainer(aviationAerAmendsSubmitRequestTaskPayload, null, EmissionTradingScheme.UK_ETS_AVIATION);
        verify(aviationAerUkEtsValidatorService, times(1)).validate(aerUkEtsContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerAmendsSubmitRequestTaskPayload, accountInfo, null, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED, appUser.getUserId());
        verify(aviationReportableEmissionsService, times(1)).deleteReportableEmissions(accountId, aerUkEtsContainer.getReportingYear());

        verify(aviationReportableEmissionsService, never()).updateReportableEmissions(any(), any(), anyBoolean());
    }
}