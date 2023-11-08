package uk.gov.pmrv.api.workflow.bpmn.handler.permittransfer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification.PermitTransferBGrantedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class PermitTransferBGrantedGenerateDocumentsHandler implements JavaDelegate {

    private final PermitTransferBGrantedGenerateDocumentsService service;
    
    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.generateDocuments(requestId);
    }
}