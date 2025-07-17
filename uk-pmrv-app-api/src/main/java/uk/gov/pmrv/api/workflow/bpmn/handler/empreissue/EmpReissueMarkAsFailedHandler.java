package uk.gov.pmrv.api.workflow.bpmn.handler.empreissue;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class EmpReissueMarkAsFailedHandler implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.setVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED, false);
		execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
	}

}
