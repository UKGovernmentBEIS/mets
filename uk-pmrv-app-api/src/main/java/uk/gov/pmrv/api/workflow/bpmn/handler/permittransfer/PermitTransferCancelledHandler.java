package uk.gov.pmrv.api.workflow.bpmn.handler.permittransfer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferCancelledService;

@Service
@RequiredArgsConstructor
public class PermitTransferCancelledHandler implements JavaDelegate {

    private final PermitTransferCancelledService service;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        service.cancel((String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
