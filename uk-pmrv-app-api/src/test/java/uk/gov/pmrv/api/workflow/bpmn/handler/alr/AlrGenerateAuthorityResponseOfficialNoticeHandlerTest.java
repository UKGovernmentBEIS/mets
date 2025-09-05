package uk.gov.pmrv.api.workflow.bpmn.handler.alr;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALROfficialNoticeService;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AlrGenerateAuthorityResponseOfficialNoticeHandlerTest {

    @InjectMocks
    private AlrGenerateAuthorityResponseOfficialNoticeHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private ALROfficialNoticeService alrOfficialNoticeService;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(alrOfficialNoticeService, times(1))
                .generateAndSaveAuthorityResponseOfficialNotice(requestId);
    }
}
