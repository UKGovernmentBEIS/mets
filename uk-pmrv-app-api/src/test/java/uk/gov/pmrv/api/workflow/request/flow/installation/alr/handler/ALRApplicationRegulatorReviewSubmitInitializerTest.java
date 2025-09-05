package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.allowance.service.AllowanceQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;

import java.time.Year;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationRegulatorReviewSubmitInitializerTest {

    @InjectMocks
    private ALRApplicationRegulatorReviewSubmitInitializer initializer;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Mock
    private AllowanceQueryService allowanceQueryService;

    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "ALR00001-2025";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.ALR)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(ALRRequestPayload
                        .builder()
                        .alr(ALR
                                .builder()
                                .build())
                        .build()
                )
                .metadata(ALRRequestMetaData.builder().year(Year.of(2025)).build())
                .build();

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .alr(((ALRRequestPayload) request.getPayload()).getAlr())
                .alrAttachments(((ALRRequestPayload) request.getPayload()).getAlrAttachments())
                .alrSectionsCompleted(((ALRRequestPayload) request.getPayload()).getAlrSectionsCompleted())
                .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().build())
                .alrFileVersion(1)
                .build();

        when(allowanceQueryService.getHistoricalActivityLevelsByAccount(accountId)).thenReturn(new ArrayList<>());

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload actualTaskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);

        verify(requestVerificationService, times(1))
                .refreshVerificationReportVBDetails(any(), eq(request.getVerificationBodyId()));
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT);
    }
}
