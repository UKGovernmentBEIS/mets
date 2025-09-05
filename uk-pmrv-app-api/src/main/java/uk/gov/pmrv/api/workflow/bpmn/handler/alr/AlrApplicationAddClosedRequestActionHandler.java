package uk.gov.pmrv.api.workflow.bpmn.handler.alr;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRCloseService;

@Service
@RequiredArgsConstructor
public class AlrApplicationAddClosedRequestActionHandler implements JavaDelegate {

    private final ALRCloseService alrCloseService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        alrCloseService.addClosedRequestAction(requestId);
    }
}
