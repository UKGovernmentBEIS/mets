package uk.gov.pmrv.api.workflow.bpmn.handler.noncompliance;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceClosedAddRequestActionService;

@Service
@RequiredArgsConstructor
public class NonComplianceClosedAddRequestActionHandler implements JavaDelegate {

    private final NonComplianceClosedAddRequestActionService service;
    
    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.addRequestAction(requestId);
    }
}
