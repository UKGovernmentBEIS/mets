package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
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
class AviationAerCorsiaApplicationReviewInitializerTest {

    @InjectMocks
    private AviationAerCorsiaApplicationReviewInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Mock
    private AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper;

    @Test
    void initializePayload() {
        String requestId = "REQ_ID";
        Long accountId = 1L;
        Long vbId = 2L;
        Year reportingYear = Year.of(2023);
        AviationAerCorsiaRequestMetadata requestMetadata = AviationAerCorsiaRequestMetadata.builder().year(reportingYear).build();
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder().build())
            .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
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
        verify(aviationAerCorsiaReviewMapper, times(1))
            .toAviationAerCorsiaApplicationReviewRequestTaskPayload(requestPayload, accountInfo, RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_PAYLOAD, reportingYear);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsOnly(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_REVIEW);
    }

    @Test
    void getRequestTaskPayloadType() {
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_PAYLOAD, initializer.getRequestTaskPayloadType());
    }
}