package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.hseti;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIOfficialNoticeService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HsetiSendOfficialNoticeHandlerFlowableTest {

    @Mock
    private HSETIOfficialNoticeService officialNoticeService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private HsetiSendOfficialNoticeHandlerFlowable handler;

    @Test
    void execute_shouldCallOfficialNoticeService() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn("REQ-1");

        handler.execute(execution);

        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(officialNoticeService).sendOfficialNotice("REQ-1");
        verifyNoMoreInteractions(officialNoticeService);
    }
}
