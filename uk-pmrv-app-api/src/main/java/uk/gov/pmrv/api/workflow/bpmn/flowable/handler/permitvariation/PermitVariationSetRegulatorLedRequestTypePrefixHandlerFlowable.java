package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitvariation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class PermitVariationSetRegulatorLedRequestTypePrefixHandlerFlowable implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		execution.setVariable(BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.PERMIT_VARIATION_REGULATOR_LED.getCode());
	}
}
