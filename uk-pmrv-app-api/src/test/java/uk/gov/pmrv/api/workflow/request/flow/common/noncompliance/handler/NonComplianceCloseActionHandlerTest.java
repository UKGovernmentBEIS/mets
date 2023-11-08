package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceApplyService;

@ExtendWith(MockitoExtension.class)
class NonComplianceCloseActionHandlerTest {
    
    @InjectMocks
    private NonComplianceCloseActionHandler handler;
    
    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceApplyService applyService;

    @Mock
    private WorkflowService workflowService;
    
    @Test
    void process() {
        
        final long requestTaskId = 1L;
        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .type(RequestTaskType.NON_COMPLIANCE_APPLICATION_SUBMIT)
            .processTaskId(processTaskId)
            .build();
        final NonComplianceCloseApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceCloseApplicationRequestTaskActionPayload.builder().build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION, pmrvUser, taskActionPayload);
        
        verify(applyService, times(1)).applyCloseAction(requestTask, taskActionPayload);
        verify(workflowService, times(1)).completeTask(
            processTaskId, Map.of(BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.CLOSED)
        );
    }
    
    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION);
    }
}
