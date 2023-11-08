package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.service.EmpVariationCorsiaSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@RequiredArgsConstructor
@Component
public class EmpVariationCorsiaSubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload>{

	private final RequestTaskService requestTaskService;
	private final EmpVariationCorsiaSubmitService empVariationCorsiaSubmitService;
	private final WorkflowService workflowService;

	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
			RequestTaskActionEmptyPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		
		//submit EMP CORSIA variation
		empVariationCorsiaSubmitService.submitEmpVariation(requestTask, pmrvUser);
		
		//set request's submission date
        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());
		
		//complete task
        workflowService.completeTask(requestTask.getProcessTaskId(), 
        		Map.of(BpmnProcessConstants.EMP_VARIATION_SUBMIT_OUTCOME, EmpVariationSubmitOutcome.SUBMITTED));
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_SUBMIT_APPLICATION);
	}
}
