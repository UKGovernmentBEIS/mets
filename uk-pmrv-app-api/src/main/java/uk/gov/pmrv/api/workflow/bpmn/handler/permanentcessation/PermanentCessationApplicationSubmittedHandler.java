package uk.gov.pmrv.api.workflow.bpmn.handler.permanentcessation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationSubmittedService;

@Service
@RequiredArgsConstructor
public class PermanentCessationApplicationSubmittedHandler implements JavaDelegate {

    private final PermanentCessationSubmittedService permanentCessationSubmittedService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        permanentCessationSubmittedService.submit(requestId);
    }
}
