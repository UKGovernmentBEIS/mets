package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsiaAnnualOffsettingSubmittedGenerateOfficialNoticeHandlerTest {


    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingSubmittedGenerateOfficialNoticeHandler handler;

    @Mock
    private AviationAerCorsiaAnnualOffsettingOfficialNoticeService aviationAerCorsiaAnnualOffsettingOfficialNoticeService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(aviationAerCorsiaAnnualOffsettingOfficialNoticeService, times(1)).generateAviationAerCorsiaAnnualOffsettingSubmittedOfficialNotice(requestId);
    }
}
