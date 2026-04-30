package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.corsia;


import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsia3YearPeriodOffsettingSubmittedSendOfficialNoticeHandlerFlowableTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingSubmittedSendOfficialNoticeHandlerFlowable handler;

    @Mock
    private  AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService officialNoticeService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(officialNoticeService, times(1)).sendOfficialNotice(requestId);
    }
}
