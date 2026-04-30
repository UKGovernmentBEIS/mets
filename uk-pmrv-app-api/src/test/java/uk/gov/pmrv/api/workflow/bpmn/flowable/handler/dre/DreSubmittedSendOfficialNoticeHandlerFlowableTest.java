package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.dre;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreOfficialNoticeSendService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DreSubmittedSendOfficialNoticeHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private DreOfficialNoticeSendService dreOfficialNoticeSendService;

    @InjectMocks
    private DreSubmittedSendOfficialNoticeHandlerFlowable handler;

    @Test
    void execute_callsSendOfficialNotice_withRequestId() {
        String requestId = "REQ-5";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(dreOfficialNoticeSendService).sendOfficialNotice(requestId);
        verifyNoMoreInteractions(dreOfficialNoticeSendService);
        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_callsSendOfficialNotice_withNullRequestId_whenMissingVariable() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);

        handler.execute(execution);

        verify(dreOfficialNoticeSendService).sendOfficialNotice(null);
        verifyNoMoreInteractions(dreOfficialNoticeSendService);
    }
}
