package uk.gov.pmrv.api.workflow.bpmn.handler.permitissuance;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceRejectedGenerateOfficialNoticeHandlerTest {

    @InjectMocks
    private PermitIssuanceRejectedGenerateOfficialNoticeHandler handler;

    @Mock
    private PermitIssuanceOfficialNoticeService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_deemed_withdrawn() {

        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(service, times(1)).generateAndSaveRejectedOfficialNotice(requestId);
    }
}
