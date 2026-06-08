package uk.gov.pmrv.api.workflow.bpmn.camunda.handler.ner;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NEROfficialNoticeService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NerSendOfficialNoticeHandlerTest {

    @Mock
    private NEROfficialNoticeService officialNoticeService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private NerSendOfficialNoticeHandler handler;

    @Test
    void execute() throws Exception {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID))
                .thenReturn("REQ1");

        handler.execute(execution);

        verify(officialNoticeService).sendOfficialNotice("REQ1");
    }
}
