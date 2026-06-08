package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AmendsOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERAmendsSubmitService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERApplicationAmendsSubmitToVerifierActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NERAmendsSubmitService amendsSubmitService;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private NERApplicationAmendsSubmitToVerifierActionHandler handler;

    @Test
    void process() {
        // given
        Long requestTaskId = 1L;

        NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload payload =
                NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload.builder()
                        .build();

        Request request = Request.builder()
                .id("requestId")
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId("processTaskId")
                .build();

        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId))
                .thenReturn(requestTask);

        // when
        handler.process(
                requestTaskId,
                RequestTaskActionType.NER_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER,
                appUser,
                payload);

        // then
        verify(requestTaskService).findTaskById(requestTaskId);

        verify(amendsSubmitService)
                .sendAmendsToVerifier(payload, requestTask, appUser);

        verify(workflowService).completeTask(
                "processTaskId",
                Map.of(
                        BpmnProcessConstants.REQUEST_ID, "requestId",
                        BpmnProcessConstants.AMENDS_OUTCOME, AmendsOutcome.SUBMITTED_TO_VERIFIER,
                        BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX,
                        RequestCustomContext.NER_AMEND.getCode()
                ));
    }

    @Test
    void getTypes() {
        assertEquals(
                List.of(RequestTaskActionType.NER_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER),
                handler.getTypes());
    }
}
