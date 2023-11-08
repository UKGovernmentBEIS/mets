package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.dre.ukets;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.bpmn.handler.aviation.dre.ukets.AviationDreSubmittedSendOfficialNoticeHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service.AviationDreOfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDreSubmittedSendOfficialNoticeHandlerTest {

    @InjectMocks
    private AviationDreSubmittedSendOfficialNoticeHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private AviationDreOfficialNoticeSendService officialNoticeSendService;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(officialNoticeSendService, times(1)).sendOfficialNotice(requestId);
    }
}
