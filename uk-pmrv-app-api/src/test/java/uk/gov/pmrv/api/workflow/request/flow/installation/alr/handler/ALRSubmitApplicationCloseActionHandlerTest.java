package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRCloseValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRSubmitApplicationCloseActionHandlerTest {

    @InjectMocks
    private ALRSubmitApplicationCloseActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private ALRCloseValidator alrCloseValidator;

    @Mock
    private ALRSubmitService alrSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().userId("userId").build();
        final RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder().build();

        final String processTaskId = "processTaskId";
        final String requestId = "AEM";
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(Request.builder().id(requestId).build())
                .build();

        final Map<String, Object> variables = Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.ALR_REGULATOR_REVIEW_OUTCOME, ALROutcome.SUBMITTED.name(),
                BpmnProcessConstants.ALR_DETERMINATION, DoalDeterminationType.CLOSED_ALR);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.ALR_CLOSE_APPLICATION, user, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(alrCloseValidator, times(1)).validate(requestTask);
        verify(alrSubmitService, times(1)).complete(requestTask);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.ALR_CLOSE_APPLICATION);
    }
}
