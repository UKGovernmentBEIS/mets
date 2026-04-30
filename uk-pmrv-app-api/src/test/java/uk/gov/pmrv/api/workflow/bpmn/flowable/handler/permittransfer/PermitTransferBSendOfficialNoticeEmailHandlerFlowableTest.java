package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permittransfer;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceOfficialNoticeService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferBSendOfficialNoticeEmailHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermitIssuanceOfficialNoticeService service;

    @InjectMocks
    private PermitTransferBSendOfficialNoticeEmailHandlerFlowable handler;

    @Test
    void execute_callsSendOfficialNoticeWithRequestIdFromExecution() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        handler.execute(execution);
        verify(service).sendOfficialNotice(requestId);
        verifyNoMoreInteractions(service);
    }
}
