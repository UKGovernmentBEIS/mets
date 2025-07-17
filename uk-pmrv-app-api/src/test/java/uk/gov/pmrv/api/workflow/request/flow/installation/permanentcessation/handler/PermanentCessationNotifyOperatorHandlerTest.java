package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.validation.PermanentCessationSubmissionValidator;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PermanentCessationNotifyOperatorHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermanentCessationService permanentCessationService;

    @Mock
    private PermanentCessationSubmissionValidator permanentCessationSubmissionValidator;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private PermanentCessationNotifyOperatorHandler handler;

    @Mock
    private RequestTask requestTask;

    @Mock
    private Request request;

    @Mock
    private AppUser appUser;

    @Mock
    private NotifyOperatorForDecisionRequestTaskActionPayload payload;

    private static final Long REQUEST_TASK_ID = 1L;

    @BeforeEach
    void setUp() { }

    @Test
    void process_shouldValidateSaveAndCompleteTask() {

        when(requestTaskService.findTaskById(REQUEST_TASK_ID)).thenReturn(requestTask);
        when(requestTask.getRequest()).thenReturn(request);
        when(requestTask.getRequest().getId()).thenReturn("test-request-id");
        when(requestTask.getProcessTaskId()).thenReturn("process-task-id");

        handler.process(REQUEST_TASK_ID, RequestTaskActionType.PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION, appUser, payload);

        verify(permanentCessationSubmissionValidator, times(1)).validate(requestTask, payload, appUser);
        verify(permanentCessationService, times(1)).savePermanentCessationDecisionNotification(payload, requestTask);
        verify(workflowService, times(1)).completeTask(
                eq("process-task-id"),
                eq(Map.of(
                        BpmnProcessConstants.REQUEST_ID, "test-request-id",
                        BpmnProcessConstants.PERMANENT_CESSATION_SUBMIT_OUTCOME, PermanentCessationSubmitOutcome.SUBMITTED
                ))
        );
    }

    @Test
    void getTypes_shouldReturnCorrectActionTypes() {
        List<RequestTaskActionType> types = handler.getTypes();
        assert(types.contains(RequestTaskActionType.PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION));
    }
}
