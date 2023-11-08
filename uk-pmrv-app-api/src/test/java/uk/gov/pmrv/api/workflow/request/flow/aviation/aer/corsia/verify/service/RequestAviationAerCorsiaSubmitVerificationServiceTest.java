package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaGeneralInformation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.validation.AviationAerCorsiaVerificationReportValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.mapper.AviationAerCorsiaVerifyMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAviationAerCorsiaSubmitVerificationServiceTest {

    @InjectMocks
    private RequestAviationAerCorsiaSubmitVerificationService submitVerificationService;

    @Mock
    private AviationAerCorsiaVerificationReportValidatorService verificationReportValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AviationAerCorsiaVerifyMapper aviationAerCorsiaVerifyMapper;

    @Test
    void submitVerificationReport() {
        Long accountId = 1L;
        BigDecimal totalEmissionsProvided = BigDecimal.valueOf(14500);
        BigDecimal totalOffsetEmissionsProvided = BigDecimal.valueOf(10500);
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .generalInformation(AviationAerCorsiaGeneralInformation.builder()
                    .verificationCriteria("verificationCriteria")
                    .operatorData("operatorData")
                    .build())
                .build())
            .build();
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
            .build();
        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload =
            AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload.builder()
                .verificationReport(verificationReport)
                .totalEmissionsProvided(totalEmissionsProvided)
                .totalOffsetEmissionsProvided(totalOffsetEmissionsProvided)
                .aer(aer)
                .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
            .reportingRequired(Boolean.TRUE)
            .aer(aer)
            .build();
        Request request = Request.builder().payload(requestPayload).verificationBodyId(2L).accountId(accountId).build();
        RequestTask requestTask = RequestTask.builder().payload(verificationSubmitRequestTaskPayload).request(request).build();
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().build();
        AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload verificationSubmittedRequestActionPayload =
            AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload.builder().build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerCorsiaVerifyMapper.toAviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload(
            verificationSubmitRequestTaskPayload,
            accountInfo,
            RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD))
            .thenReturn(verificationSubmittedRequestActionPayload);

        submitVerificationService.submitVerificationReport(requestTask, pmrvUser);

        AviationAerCorsiaRequestPayload updatedRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertTrue(updatedRequestPayload.getReportingRequired());
        assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(verificationReport, updatedRequestPayload.getVerificationReport());
        assertEquals(totalEmissionsProvided, updatedRequestPayload.getTotalEmissionsProvided());
        assertEquals(totalOffsetEmissionsProvided, updatedRequestPayload.getTotalOffsetEmissionsProvided());

        verify(verificationReportValidatorService, times(1)).validate(verificationReport, aer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaVerifyMapper, times(1))
            .toAviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload(
                verificationSubmitRequestTaskPayload,
                accountInfo,
                RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request,
            verificationSubmittedRequestActionPayload,
            RequestActionType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED,
            pmrvUser.getUserId());
    }
}