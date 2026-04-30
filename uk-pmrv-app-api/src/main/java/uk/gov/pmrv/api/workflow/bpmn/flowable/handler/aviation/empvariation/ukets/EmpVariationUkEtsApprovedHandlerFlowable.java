package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsUpdateEmpService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsApprovedHandlerFlowable implements JavaDelegate {

	private final EmpVariationUkEtsUpdateEmpService empVariationUkEtsUpdateEmpService;

	@Override
	public void execute(DelegateExecution execution) {
		String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		empVariationUkEtsUpdateEmpService.updateEmp(requestId);
	}
}
