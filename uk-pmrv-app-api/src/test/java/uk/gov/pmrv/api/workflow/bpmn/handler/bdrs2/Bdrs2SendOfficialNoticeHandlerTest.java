package uk.gov.pmrv.api.workflow.bpmn.handler.bdrs2;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2OfficialNoticeService;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class Bdrs2SendOfficialNoticeHandlerTest {

    @InjectMocks
    private Bdrs2SendOfficialNoticeHandler handler;

    @Mock
    private BDRS2OfficialNoticeService bdrs2OfficialNoticeService;

    @Mock
    private DelegateExecution execution;


    @Test
    void execute() throws Exception {

        String requestId = "BDRS2-00001-2025";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(bdrs2OfficialNoticeService, times(1)).sendOfficialNotice(requestId);
    }
}
