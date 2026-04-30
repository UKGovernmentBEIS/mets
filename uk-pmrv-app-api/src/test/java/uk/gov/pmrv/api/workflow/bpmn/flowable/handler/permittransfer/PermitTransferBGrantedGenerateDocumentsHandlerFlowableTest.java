package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permittransfer;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification.PermitTransferBGrantedGenerateDocumentsService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferBGrantedGenerateDocumentsHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermitTransferBGrantedGenerateDocumentsService service;

    @InjectMocks
    private PermitTransferBGrantedGenerateDocumentsHandlerFlowable handler;

    @Test
    void execute_callsGenerateDocumentsWithRequestIdFromExecution() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        handler.execute(execution);
        verify(service).generateDocuments(requestId);
        verifyNoMoreInteractions(service);
    }
}
