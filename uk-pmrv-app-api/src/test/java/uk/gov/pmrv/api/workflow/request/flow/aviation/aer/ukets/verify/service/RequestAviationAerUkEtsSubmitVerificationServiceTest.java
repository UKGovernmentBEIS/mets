package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerVerificationTeamDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerVerifierContact;
import uk.gov.pmrv.api.aviationreporting.ukets.validation.AviationAerUkEtsVerificationReportValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.mapper.AviationAerUkEtsVerifyMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAviationAerUkEtsSubmitVerificationServiceTest {

    @InjectMocks
    private RequestAviationAerUkEtsSubmitVerificationService submitVerificationService;

    @Mock
    private AviationAerUkEtsVerificationReportValidatorService verificationReportValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AviationAerUkEtsVerifyMapper aviationAerUkEtsVerifyMapper;

    @Test
    void submitVerificationReport() {
        Long accountId = 1L;
        BigDecimal totalEmissionsProvided = BigDecimal.valueOf(14500);
        String notCoveredChangesProvided = "not covered changes";
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .verifierContact(AviationAerVerifierContact.builder().name("name").build())
                .verificationTeamDetails(AviationAerVerificationTeamDetails.builder().leadEtsAuditor("leadEtsAuditor").build())
                .build())
            .build();
        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload.builder()
                .verificationReport(verificationReport)
                .totalEmissionsProvided(totalEmissionsProvided)
                .notCoveredChangesProvided(notCoveredChangesProvided)
                .build();
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
            .build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .reportingRequired(Boolean.TRUE)
            .aer(aer)
            .build();
        Request request = Request.builder().payload(requestPayload).verificationBodyId(2L).accountId(accountId).build();
        RequestTask requestTask = RequestTask.builder().payload(verificationSubmitRequestTaskPayload).request(request).build();
        AppUser appUser = AppUser.builder().userId("userId").build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().build();
        AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload verificationSubmittedRequestActionPayload =
            AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload.builder().build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerUkEtsVerifyMapper.toAviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload(
            verificationSubmitRequestTaskPayload,
            accountInfo,
            RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD))
            .thenReturn(verificationSubmittedRequestActionPayload);

        submitVerificationService.submitVerificationReport(requestTask, appUser);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertTrue(updatedRequestPayload.getReportingRequired());
        assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(verificationReport, updatedRequestPayload.getVerificationReport());
        assertEquals(totalEmissionsProvided, updatedRequestPayload.getTotalEmissionsProvided());
        assertEquals(notCoveredChangesProvided, updatedRequestPayload.getNotCoveredChangesProvided());

        verify(verificationReportValidatorService, times(1)).validate(verificationReport);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsVerifyMapper, times(1))
            .toAviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload(
                verificationSubmitRequestTaskPayload,
                accountInfo,
                RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request,
            verificationSubmittedRequestActionPayload,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED,
            appUser.getUserId());
    }

    @Test
    void submitVerificationReport_amends() {
        Long accountId = 1L;
        BigDecimal totalEmissionsProvided = BigDecimal.valueOf(14500);
        String notCoveredChangesProvided = "not covered changes";
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .safExists(false)
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .verifierContact(AviationAerVerifierContact.builder().name("name").build())
                .verificationTeamDetails(AviationAerVerificationTeamDetails.builder().leadEtsAuditor("leadEtsAuditor").build())
                .build())
            .build();
        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload.builder()
                .verificationReport(verificationReport)
                .totalEmissionsProvided(totalEmissionsProvided)
                .notCoveredChangesProvided(notCoveredChangesProvided)
                .build();
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
            .build();
        AerVerificationReportDataReviewDecision verificationReportDataReviewDecision =
            AerVerificationReportDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
                .type(AerVerificationReportDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AviationAerUkEtsReviewGroup.class);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.VERIFIER_DETAILS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OVERALL_DECISION, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM_VERIFICATION, verificationReportDataReviewDecision);
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .reportingRequired(Boolean.TRUE)
            .aer(aer)
            .verificationReport(AviationAerUkEtsVerificationReport.builder().safExists(true).build())
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();
        Request request = Request.builder().payload(requestPayload).verificationBodyId(2L).accountId(accountId).build();
        RequestTask requestTask = RequestTask.builder().payload(verificationSubmitRequestTaskPayload).request(request).build();
        AppUser appUser = AppUser.builder().userId("userId").build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().build();
        AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload verificationSubmittedRequestActionPayload =
            AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload.builder().build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerUkEtsVerifyMapper.toAviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload(
            verificationSubmitRequestTaskPayload,
            accountInfo,
            RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD))
            .thenReturn(verificationSubmittedRequestActionPayload);

        submitVerificationService.submitVerificationReport(requestTask, appUser);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertTrue(updatedRequestPayload.getReportingRequired());
        assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(verificationReport, updatedRequestPayload.getVerificationReport());
        assertEquals(totalEmissionsProvided, updatedRequestPayload.getTotalEmissionsProvided());
        assertEquals(notCoveredChangesProvided, updatedRequestPayload.getNotCoveredChangesProvided());
        assertThat(updatedRequestPayload.getReviewGroupDecisions().keySet())
            .containsExactlyInAnyOrder(AviationAerUkEtsReviewGroup.VERIFIER_DETAILS, AviationAerUkEtsReviewGroup.OVERALL_DECISION);

        verify(verificationReportValidatorService, times(1)).validate(verificationReport);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsVerifyMapper, times(1))
            .toAviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload(
                verificationSubmitRequestTaskPayload,
                accountInfo,
                RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request,
            verificationSubmittedRequestActionPayload,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED,
            appUser.getUserId());
    }
}