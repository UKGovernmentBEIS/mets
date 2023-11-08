package uk.gov.pmrv.api.workflow.bpmn.handler.doal;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalAuthorityResponseService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalApplicationAddRejectedRequestActionHandlerTest {

    @InjectMocks
    private DoalApplicationAddRejectedRequestActionHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private DoalAuthorityResponseService doalAuthorityResponseService;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(doalAuthorityResponseService, times(1))
                .addSubmittedRequestAction(requestId, RequestActionType.DOAL_APPLICATION_REJECTED);
    }
}
