package uk.gov.pmrv.api.workflow.bpmn.handler.bdrs2;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2CompleteService;

@Service
@RequiredArgsConstructor
public class Bdrs2CompletedAddRequestActionHandler implements JavaDelegate {

    private final BDRS2CompleteService bdrs2CompleteService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        bdrs2CompleteService.addRequestAction(requestId);
    }
}
