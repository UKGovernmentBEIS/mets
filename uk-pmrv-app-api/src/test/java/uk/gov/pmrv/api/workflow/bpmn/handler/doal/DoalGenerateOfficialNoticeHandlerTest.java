package uk.gov.pmrv.api.workflow.bpmn.handler.doal;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalOfficialNoticeService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalGenerateOfficialNoticeHandlerTest {

    @InjectMocks
    private DoalGenerateOfficialNoticeHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private DoalOfficialNoticeService doalOfficialNoticeService;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(doalOfficialNoticeService, times(1))
                .generateAndSaveProceededToAuthorityOfficialNotice(requestId);
    }
}
