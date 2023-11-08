package uk.gov.pmrv.api.workflow.bpmn.handler.permittransfer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferAAerService;

@Service
@RequiredArgsConstructor
public class PermitTransferAAerHandler implements JavaDelegate {
    
    private final PermitTransferAAerService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.process(requestId);
    }
}
