package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.corsia;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaSendOfficialNoticeEmailHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private EmpVariationCorsiaOfficialNoticeService service;

    @InjectMocks
    private EmpVariationCorsiaSendOfficialNoticeEmailHandlerFlowable handler;

    @Test
    void execute_callsServiceSendOfficialNotice_withRequestId() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).sendOfficialNotice(requestId);
        verifyNoMoreInteractions(service);
    }
}
