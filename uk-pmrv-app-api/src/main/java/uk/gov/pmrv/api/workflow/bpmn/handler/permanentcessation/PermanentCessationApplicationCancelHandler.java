package uk.gov.pmrv.api.workflow.bpmn.handler.permanentcessation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationService;

@Service
@RequiredArgsConstructor
public class PermanentCessationApplicationCancelHandler implements JavaDelegate {

    private final PermanentCessationService permanentCessationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        permanentCessationService.cancel(requestId);
    }
}
