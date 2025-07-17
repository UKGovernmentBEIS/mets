package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation.DoalCloseValidator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalSubmitApplicationCloseActionHandlerTest {

    @InjectMocks
    private DoalSubmitApplicationCloseActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private DoalCloseValidator doalCloseValidator;

    @Mock
    private DoalSubmitService doalSubmitService;

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
                BpmnProcessConstants.DOAL_SUBMIT_OUTCOME, DoalSubmitOutcome.SUBMITTED,
                BpmnProcessConstants.DOAL_DETERMINATION, DoalDeterminationType.CLOSED);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.DOAL_CLOSE_APPLICATION, user, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(doalCloseValidator, times(1)).validate(requestTask);
        verify(doalSubmitService, times(1)).complete(requestTask);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.DOAL_CLOSE_APPLICATION);
    }
}
