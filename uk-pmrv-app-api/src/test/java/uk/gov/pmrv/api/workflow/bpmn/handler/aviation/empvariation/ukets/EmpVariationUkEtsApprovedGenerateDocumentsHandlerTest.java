package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.ukets;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsOperatorLedApprovedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsApprovedGenerateDocumentsHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsApprovedGenerateDocumentsHandler handler;
	
	@Mock
	private EmpVariationUkEtsOperatorLedApprovedGenerateDocumentsService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
    	String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(service, times(1)).generateDocuments(requestId);
    }
}
