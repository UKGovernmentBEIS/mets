package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.corsia;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaRegulatorLedApprovedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRegulatorLedApprovedGenerateDocumentsHandlerTest {

    @InjectMocks
    private EmpVariationCorsiaRegulatorLedApprovedGenerateDocumentsHandler cut;

    @Mock
    private EmpVariationCorsiaRegulatorLedApprovedGenerateDocumentsService service;

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
