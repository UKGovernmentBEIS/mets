package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALROfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class AlrGenerateAuthorityResponseOfficialNoticeHandlerFlowableTest {

    @Mock
    private ALROfficialNoticeService alrOfficialNoticeService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private AlrGenerateAuthorityResponseOfficialNoticeHandlerFlowable handler;

    @Test
    void execute_shouldGenerateAndSaveAuthorityResponseOfficialNotice() {
        String requestId = "REQ-12345";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        verify(alrOfficialNoticeService).generateAndSaveAuthorityResponseOfficialNotice(requestId);
    }

    @Test
    void execute_shouldPassNullRequestId_whenExecutionVariableMissing() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);
        handler.execute(execution);
        verify(alrOfficialNoticeService).generateAndSaveAuthorityResponseOfficialNotice(null);
    }
}
