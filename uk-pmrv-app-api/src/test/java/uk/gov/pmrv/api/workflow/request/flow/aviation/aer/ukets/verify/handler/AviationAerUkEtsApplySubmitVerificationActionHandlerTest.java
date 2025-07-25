package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.service.RequestAviationAerUkEtsSubmitVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplySubmitVerificationActionHandlerTest {

    @InjectMocks
    private AviationAerUkEtsApplySubmitVerificationActionHandler submitVerificationActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerUkEtsSubmitVerificationService submitVerificationService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        AppUser user = AppUser.builder().build();
        RequestTaskActionEmptyPayload taskActionPayload = RequestTaskActionEmptyPayload.builder().build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        submitVerificationActionHandler.process(
            requestTaskId,
            RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_VERIFICATION,
            user,
            taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(submitVerificationService, times(1)).submitVerificationReport(requestTask, user);
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(submitVerificationActionHandler.getTypes())
            .containsOnly(RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_VERIFICATION);
    }
}