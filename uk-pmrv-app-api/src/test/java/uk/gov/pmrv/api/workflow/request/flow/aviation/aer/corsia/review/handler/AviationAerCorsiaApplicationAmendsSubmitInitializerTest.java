package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private AviationAerCorsiaApplicationAmendsSubmitInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper;

    @Test
    void initializePayload() {
        String requestId = "REQ_ID";
        Long accountId = 1L;
        Year reportingYear = Year.of(2023);
        AviationAerRequestMetadata requestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder().build();
        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .payload(requestPayload)
                .metadata(requestMetadata)
                .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerCorsiaReviewMapper.toAviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload(requestPayload,
                accountInfo,
                RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD,
                reportingYear))
                .thenReturn(AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                        .reportingYear(reportingYear)
                        .build());

        //invoke
        final RequestTaskPayload actual = initializer.initializePayload(request);

        //verify
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaReviewMapper, times(1))
                .toAviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload(
                        requestPayload,
                        accountInfo,
                        RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD,
                        reportingYear
                );
        assertThat(actual).isInstanceOf(AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.class);
        assertThat(actual.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD);
        assertThat(((AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload)actual).getReportingYear()).isEqualTo(reportingYear);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsOnly(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT);
    }
}
