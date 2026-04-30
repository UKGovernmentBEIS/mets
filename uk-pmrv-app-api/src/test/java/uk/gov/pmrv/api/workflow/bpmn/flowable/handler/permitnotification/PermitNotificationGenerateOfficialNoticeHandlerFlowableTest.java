package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationOfficialNoticeService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationGenerateOfficialNoticeHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermitNotificationOfficialNoticeService service;

    @InjectMocks
    private PermitNotificationGenerateOfficialNoticeHandlerFlowable handler;

    @Test
    void execute_whenGranted_generatesGrantedOfficialNotice() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION))
                .thenReturn(DeterminationType.GRANTED);

        handler.execute(execution);

        verify(service).generateAndSaveGrantedOfficialNotice(requestId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void execute_whenRejected_generatesRejectedOfficialNotice() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION))
                .thenReturn(DeterminationType.REJECTED);

        handler.execute(execution);

        verify(service).generateAndSaveRejectedOfficialNotice(requestId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void execute_whenCompleted_generatesCompletedOfficialNotice() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION))
                .thenReturn(DeterminationType.COMPLETED);

        handler.execute(execution);

        verify(service).generateAndSaveCompletedOfficialNotice(requestId);
        verifyNoMoreInteractions(service);
    }
}
