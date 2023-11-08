package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PermitVariationSetRegulatorLedRequestTypePrefixHandler implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.setVariable(BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.PERMIT_VARIATION_REGULATOR_LED.getCode());
	}
}
