package uk.gov.pmrv.api.workflow.bpmn.handler.dre;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreOfficialNoticeSendService;

@ExtendWith(MockitoExtension.class)
class DreSubmittedSendOfficialNoticeHandlerTest {

	@InjectMocks
    private DreSubmittedSendOfficialNoticeHandler cut;

    @Mock
    private DreOfficialNoticeSendService dreOfficialNoticeSendService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        cut.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(dreOfficialNoticeSendService, times(1)).sendOfficialNotice(requestId);
    }
}