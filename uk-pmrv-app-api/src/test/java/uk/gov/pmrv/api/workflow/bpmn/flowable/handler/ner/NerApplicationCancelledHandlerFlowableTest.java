package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.ner;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplicationCancelledService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NerApplicationCancelledHandlerFlowableTest {

    @Mock
    private NerApplicationCancelledService service;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private NerApplicationCancelledHandlerFlowable handler;

    @Test
    void execute() {
        String requestId = "REQ-1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID))
                .thenReturn(requestId);

        handler.execute(execution);

        verify(service).cancel(requestId);
    }
}
