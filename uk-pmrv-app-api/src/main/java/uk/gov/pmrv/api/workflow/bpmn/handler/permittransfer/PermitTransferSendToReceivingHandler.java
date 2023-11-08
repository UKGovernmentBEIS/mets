package uk.gov.pmrv.api.workflow.bpmn.handler.permittransfer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBInitiateRequestService;

@Service
@RequiredArgsConstructor
public class PermitTransferSendToReceivingHandler implements JavaDelegate {
    
    private final PermitTransferBInitiateRequestService service;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {

        final String transferARequestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String transferABusinessKey = (String) delegateExecution.getVariable(BpmnProcessConstants.BUSINESS_KEY);

        final String transferBBusinessKey = service.initiatePermitTransferBRequest(transferARequestId, transferABusinessKey);

        delegateExecution.setVariable(BpmnProcessConstants.PERMIT_TRANSFER_RECEIVING_BUSINESS_KEY, transferBBusinessKey);
    }
}
