package uk.gov.pmrv.api.workflow.bpmn.handler.alr;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAuthorityResponseService;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AlrApplicationAddRejectedRequestActionHandlerTest {

    @InjectMocks
    private AlrApplicationAddRejectedRequestActionHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private ALRAuthorityResponseService alrAuthorityResponseService;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(alrAuthorityResponseService, times(1))
                .addSubmittedRequestAction(requestId, RequestActionType.ALR_APPLICATION_REJECTED);
    }
}
