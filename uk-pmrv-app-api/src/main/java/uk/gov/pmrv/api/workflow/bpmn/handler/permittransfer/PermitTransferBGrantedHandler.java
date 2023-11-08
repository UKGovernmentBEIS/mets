package uk.gov.pmrv.api.workflow.bpmn.handler.permittransfer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBGrantedService;

@Service
@RequiredArgsConstructor
public class PermitTransferBGrantedHandler implements JavaDelegate {

    private final PermitTransferBGrantedService service;

    @Override
    public void execute(DelegateExecution execution) {
        
        service.grant((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
