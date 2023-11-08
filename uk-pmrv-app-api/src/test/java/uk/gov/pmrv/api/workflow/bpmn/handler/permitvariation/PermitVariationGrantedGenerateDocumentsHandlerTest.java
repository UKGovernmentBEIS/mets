package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationOperatorLedGrantedGenerateDocumentsService;

@ExtendWith(MockitoExtension.class)
class PermitVariationGrantedGenerateDocumentsHandlerTest {
	
	@InjectMocks
    private PermitVariationGrantedGenerateDocumentsHandler cut;

    @Mock
    private PermitVariationOperatorLedGrantedGenerateDocumentsService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        cut.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(service, times(1)).generateDocuments(requestId);
    }
}
