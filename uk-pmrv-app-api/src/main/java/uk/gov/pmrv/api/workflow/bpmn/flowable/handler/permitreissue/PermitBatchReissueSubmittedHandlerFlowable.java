package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitreissue;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.BatchReissueSubmittedService;

@Service
@RequiredArgsConstructor
public class PermitBatchReissueSubmittedHandlerFlowable implements JavaDelegate {

	private final BatchReissueSubmittedService service;
	
	@Override
	public void execute(DelegateExecution execution) {
		service.batchReissueSubmitted((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}

}
