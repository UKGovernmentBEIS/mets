package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.ukets;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsUpdateEmpService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsApprovedHandler implements JavaDelegate {
	
	private final EmpVariationUkEtsUpdateEmpService empVariationUkEtsUpdateEmpService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		empVariationUkEtsUpdateEmpService.updateEmp(requestId);
	}
}
