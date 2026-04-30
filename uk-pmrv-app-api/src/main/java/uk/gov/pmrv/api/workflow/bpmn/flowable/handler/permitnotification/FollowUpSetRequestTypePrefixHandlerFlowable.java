package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class FollowUpSetRequestTypePrefixHandlerFlowable implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		execution.setVariable(BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.PERMIT_NOTIFICATION_FOLLOW_UP.getCode());
	}
}
