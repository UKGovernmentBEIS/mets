package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.doal;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalAuthorityResponseService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalApplicationAddAcceptedWithCorrectionsRequestActionHandlerFlowableTest {

    @Mock
    private DoalAuthorityResponseService doalAuthorityResponseService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private DoalApplicationAddAcceptedWithCorrectionsRequestActionHandlerFlowable handler;

    @Test
    void execute_shouldAddAcceptedWithCorrectionsSubmittedRequestAction() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(doalAuthorityResponseService)
            .addSubmittedRequestAction(requestId, RequestActionType.DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS);
        verifyNoMoreInteractions(doalAuthorityResponseService);
    }
}
