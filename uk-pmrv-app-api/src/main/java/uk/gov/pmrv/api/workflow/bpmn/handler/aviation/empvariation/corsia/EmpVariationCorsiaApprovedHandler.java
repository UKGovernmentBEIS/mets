package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.corsia;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaUpdateEmpService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaApprovedHandler implements JavaDelegate {

private final EmpVariationCorsiaUpdateEmpService empVariationCorsiaUpdateEmpService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		empVariationCorsiaUpdateEmpService.updateEmp(requestId);
	}
}
