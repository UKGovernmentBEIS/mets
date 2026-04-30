package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.vir;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class AviationVirGenerateOfficialNoticeHandlerFlowableTest {

    @InjectMocks
    private AviationVirGenerateOfficialNoticeHandlerFlowable handler;

    @Mock
    private AviationVirOfficialNoticeService virOfficialNoticeService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(virOfficialNoticeService, times(1)).generateAndSaveRecommendedImprovementsOfficialNotice(requestId);
    }
}
