package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.dre;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreUpdateReportableEmissionsService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DreUpdateReportableEmissionsHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private DreUpdateReportableEmissionsService service;

    @InjectMocks
    private DreUpdateReportableEmissionsHandlerFlowable handler;

    @Test
    void execute_callsUpdateReportableEmissions_withRequestId() {
        String requestId = "REQ-6";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).updateReportableEmissions(requestId);
        verifyNoMoreInteractions(service);
        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_callsUpdateReportableEmissions_withNullRequestId_whenMissingVariable() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);

        handler.execute(execution);

        verify(service).updateReportableEmissions(null);
        verifyNoMoreInteractions(service);
    }
}
