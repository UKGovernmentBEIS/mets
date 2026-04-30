package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirOfficialNoticeService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirSendOfficialNoticeEmailHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private AirOfficialNoticeService officialNoticeService;

    @InjectMocks
    private AirSendOfficialNoticeEmailHandlerFlowable handler;

    @Test
    void execute_callsSendOfficialNoticeWithRequestId() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(officialNoticeService).sendOfficialNotice(requestId);
    }
}
