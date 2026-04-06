package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class NERApplicationSubmitToVerifierActionHandlerTest {


    @InjectMocks
    private NERApplicationSubmitToVerifierActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerApplyService nerApplyService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();

        final NERApplicationSubmitToVerifierRequestTaskActionPayload payload =
                NERApplicationSubmitToVerifierRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.NER_APPLICATION_SUBMIT_TO_VERIFIER_PAYLOAD)
                        .build();

        final String processId = "processId";
        final String requestId = "requestId";

        final RequestTask task = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .processTaskId(processId)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        // when
        handler.process(taskId,
                RequestTaskActionType.NER_APPLICATION_SUBMIT_TO_VERIFIER,
                user,
                payload);

        // then
        verify(requestTaskService, times(1)).findTaskById(taskId);

        verify(nerApplyService, times(1))
                .submitToVerifier(payload, task, user);

        verify(workflowService, times(1)).completeTask(
                processId,
                Map.of(
                        BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.NER_SUBMIT_OUTCOME, NerSubmitOutcome.SUBMITTED_TO_VERIFIER
                )
        );
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(
                List.of(RequestTaskActionType.NER_APPLICATION_SUBMIT_TO_VERIFIER),
                handler.getTypes()
        );
    }
}
