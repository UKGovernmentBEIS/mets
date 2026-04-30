package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDROfficialNoticeService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BdrSendOfficialNoticeHandlerFlowableTest {

    @Mock
    private BDROfficialNoticeService officialNoticeService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private BdrSendOfficialNoticeHandlerFlowable handler;

    @Test
    void execute_callsServiceWithRequestId() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(officialNoticeService).sendOfficialNotice(requestId);
        verifyNoMoreInteractions(officialNoticeService);
    }
}
