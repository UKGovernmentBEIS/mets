package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

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
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSubmitOutcome;

@ExtendWith(MockitoExtension.class)
class DreCancelHandlerTest {

	@InjectMocks
    private DreCancelHandler cut;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private WorkflowService workflowService;
    
    @Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.DRE_CANCEL_APPLICATION;
		PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder()
				.build();
		
		RequestTask requestTask = RequestTask.builder().id(1L).build();
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType, pmrvUser, payload);
		
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.DRE_SUBMIT_OUTCOME, DreSubmitOutcome.CANCELLED));
	}
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes())
				.containsExactlyInAnyOrder(RequestTaskActionType.DRE_CANCEL_APPLICATION);
	}
}
