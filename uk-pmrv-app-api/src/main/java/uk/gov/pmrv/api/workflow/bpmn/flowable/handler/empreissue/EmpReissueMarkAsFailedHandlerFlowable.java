package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.empreissue;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class EmpReissueMarkAsFailedHandlerFlowable implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED, false);
        execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
