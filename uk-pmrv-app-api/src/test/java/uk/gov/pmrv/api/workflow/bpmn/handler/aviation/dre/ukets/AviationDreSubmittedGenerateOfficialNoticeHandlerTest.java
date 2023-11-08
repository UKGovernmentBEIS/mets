package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.dre.ukets;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.bpmn.handler.aviation.dre.ukets.AviationDreSubmittedGenerateOfficialNoticeHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service.AviationDreOfficialNoticeGenerateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDreSubmittedGenerateOfficialNoticeHandlerTest {

    @InjectMocks
    private AviationDreSubmittedGenerateOfficialNoticeHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private AviationDreOfficialNoticeGenerateService officialNoticeGenerateService;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(officialNoticeGenerateService, times(1)).generateOfficialNotice(requestId);
    }
}
