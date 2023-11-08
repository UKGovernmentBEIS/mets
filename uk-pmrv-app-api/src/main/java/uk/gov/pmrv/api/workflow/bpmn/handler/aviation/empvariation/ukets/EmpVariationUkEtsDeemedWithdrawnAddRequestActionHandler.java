package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.ukets;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsDeemedWithdrawnAddRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsDeemedWithdrawnAddRequestActionHandler implements JavaDelegate {
	
	private final EmpVariationUkEtsDeemedWithdrawnAddRequestActionService addRequestActionService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        addRequestActionService.addRequestAction(requestId);
    }
}
