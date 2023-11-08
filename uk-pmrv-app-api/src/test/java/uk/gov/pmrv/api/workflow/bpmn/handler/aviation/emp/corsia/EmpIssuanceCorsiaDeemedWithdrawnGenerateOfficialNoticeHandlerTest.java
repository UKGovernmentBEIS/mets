package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.emp.corsia;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.EmpIssuanceCorsiaOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaDeemedWithdrawnGenerateOfficialNoticeHandlerTest {

    @InjectMocks
    private EmpIssuanceCorsiaDeemedWithdrawnGenerateOfficialNoticeHandler handler;

    @Mock
    private EmpIssuanceCorsiaOfficialNoticeService empIssuanceOfficialNoticeService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(empIssuanceOfficialNoticeService, times(1)).generateAndSaveDeemedWithdrawnOfficialNotice(requestId);
    }
}
