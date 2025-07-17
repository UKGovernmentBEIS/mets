package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplicationReviewInitializerTest {

    @InjectMocks
    private AviationAerUkEtsApplicationReviewInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Mock
    private AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper;

    @Test
    void initializePayload_when_verification_report_exists() {
        String requestId = "REQ_ID";
        Long accountId = 1L;
        Long vbId = 2L;
        Year reportingYear = Year.of(2023);
        AviationAerRequestMetadata requestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder().build())
            .build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .verificationReport(verificationReport)
            .build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .verificationBodyId(vbId)
            .payload(requestPayload)
            .metadata(requestMetadata)
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("code").build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);

        //invoke
        initializer.initializePayload(request);

        //verify
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(requestVerificationService, times(1)).refreshVerificationReportVBDetails(verificationReport, vbId);
        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsApplicationReviewRequestTaskPayload(requestPayload, accountInfo, RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD, reportingYear);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsOnly(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_REVIEW);
    }

    @Test
    void getRequestTaskPayloadType() {
        assertEquals(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD, initializer.getRequestTaskPayloadType());
    }
}