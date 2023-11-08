package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirReviewSaveActionHandlerTest {

    @InjectMocks
    private VirReviewSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private VirReviewService virReviewService;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final VirSaveReviewRequestTaskActionPayload actionPayload = VirSaveReviewRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.VIR_SAVE_REVIEW_PAYLOAD)
                .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.VIR_SAVE_REVIEW, pmrvUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(virReviewService, times(1)).saveReview(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.VIR_SAVE_REVIEW);
    }
}
