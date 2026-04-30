package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.noncompliance;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceClosedAddRequestActionService;

@Service
@RequiredArgsConstructor
public class NonComplianceClosedAddRequestActionHandlerFlowable implements JavaDelegate {

    private final NonComplianceClosedAddRequestActionService service;
    
    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.addRequestAction(requestId);
    }
}
