package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.emp.corsia;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.EmpIssuanceCorsiaGrantedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaGrantedGenerateDocumentsHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private EmpIssuanceCorsiaGrantedGenerateDocumentsService empIssuanceGrantedGenerateDocumentsService;

    @InjectMocks
    private EmpIssuanceCorsiaGrantedGenerateDocumentsHandlerFlowable handler;

    @Test
    void execute_callsServiceGenerateDocuments_withRequestId() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(empIssuanceGrantedGenerateDocumentsService).generateDocuments(requestId);
        verifyNoMoreInteractions(empIssuanceGrantedGenerateDocumentsService);
    }
}
