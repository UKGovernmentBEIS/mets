package uk.gov.pmrv.api.workflow.bpmn.handler.doal;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalSubmitService;

@Service
@RequiredArgsConstructor
public class DoalApplicationAddProceededToAuthorityRequestActionHandler implements JavaDelegate {

    private final DoalSubmitService doalSubmitService;

    @Override
    public void execute(final DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        doalSubmitService.addProceededToAuthorityRequestAction(requestId);
    }
}
