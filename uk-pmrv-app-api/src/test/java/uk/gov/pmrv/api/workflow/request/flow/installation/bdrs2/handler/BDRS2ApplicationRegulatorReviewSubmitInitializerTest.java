package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BDRS2ApplicationRegulatorReviewSubmitInitializerTest {

    @InjectMocks
    private BDRS2ApplicationRegulatorReviewSubmitInitializer initializer;

    @Mock
    private RequestVerificationService requestVerificationService;


    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "BDRS2-00001-2025";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.BDRS2)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(BDRS2RequestPayload
                        .builder()
                        .bdrs2(BDRS2
                                .builder()
                                .build())
                        .build()
                )
                .metadata(BDRS2RequestMetadata.builder().year(Year.of(2025)).build())
                .build();

        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .bdrs2( ((BDRS2RequestPayload) request.getPayload()).getBdrs2())
                .bdrs2Attachments( ((BDRS2RequestPayload) request.getPayload()).getBdrs2Attachments())
                .bdrs2SectionsCompleted( ((BDRS2RequestPayload) request.getPayload()).getBdrs2SectionsCompleted())
                .bdrs2FileVersion(1)
                .build();


        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload actualTaskPayload =
                (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);


        verify(requestVerificationService, times(1))
                .refreshVerificationReportVBDetails(any(), eq(request.getVerificationBodyId()));
    }

    @Test
    void getRequestTaskTypes(){
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT);
    }
}
