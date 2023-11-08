package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirReviewService;

@ExtendWith(MockitoExtension.class)
class AviationVirReviewSaveActionHandlerTest {

    @InjectMocks
    private AviationVirReviewSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationVirReviewService virReviewService;

    @Test
    void process() {
        
        final long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final AviationVirSaveReviewRequestTaskActionPayload actionPayload =
            AviationVirSaveReviewRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_VIR_SAVE_REVIEW_PAYLOAD)
                .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.AVIATION_VIR_SAVE_REVIEW, pmrvUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(virReviewService, times(1)).saveReview(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.AVIATION_VIR_SAVE_REVIEW);
    }
}
