package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyService;

@ExtendWith(MockitoExtension.class)
class NerSaveApplicationActionHandlerTest {

    @InjectMocks
    private NerSaveApplicationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerApplyService applyService;

    @Test
    void doProcess() {

        final NerSaveApplicationRequestTaskActionPayload taskActionPayload =
            NerSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.NER_SAVE_APPLICATION_PAYLOAD)
                .build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask =
            RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.NER_SAVE_APPLICATION,
            pmrvUser,
            taskActionPayload);

        verify(applyService, times(1)).applySaveAction(requestTask, taskActionPayload);
    }
}
