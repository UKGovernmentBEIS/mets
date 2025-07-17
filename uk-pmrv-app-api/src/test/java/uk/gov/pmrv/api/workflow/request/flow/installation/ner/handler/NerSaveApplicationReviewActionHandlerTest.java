package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;

@ExtendWith(MockitoExtension.class)
class NerSaveApplicationReviewActionHandlerTest {

    @InjectMocks
    private NerSaveApplicationReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerApplyReviewService applyService;

    @Test
    void doProcess() {

        final NerSaveApplicationReviewRequestTaskActionPayload taskActionPayload =
            NerSaveApplicationReviewRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.NER_SAVE_APPLICATION_REVIEW_PAYLOAD)
                .build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask =
            RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.NER_SAVE_APPLICATION_REVIEW,
            appUser,
            taskActionPayload);

        verify(applyService, times(1)).applySaveAction(requestTask, taskActionPayload);
    }
    
    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.NER_SAVE_APPLICATION_REVIEW));
    }
}
