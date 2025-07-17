package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.service.EmpVariationCorsiaAmendService;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaSubmitApplicationAmendActionHandlerTest {

	@InjectMocks
    private EmpVariationCorsiaSubmitApplicationAmendActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EmpVariationCorsiaAmendService empVariationCorsiaAmendService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        AppUser appUser = AppUser.builder().build();
        EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload actionPayload =
            EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_VARIATION_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.EMP_VARIATION_CORSIA_SUBMIT_APPLICATION_AMEND, appUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(empVariationCorsiaAmendService, times(1)).submitAmend(actionPayload, requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsOnly(RequestTaskActionType.EMP_VARIATION_CORSIA_SUBMIT_APPLICATION_AMEND);
    }
}
