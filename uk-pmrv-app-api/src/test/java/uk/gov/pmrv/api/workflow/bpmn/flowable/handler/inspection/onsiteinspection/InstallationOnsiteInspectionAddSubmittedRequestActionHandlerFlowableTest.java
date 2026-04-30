package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.inspection.onsiteinspection;


import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionAddSubmittedRequestActionService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionAddSubmittedRequestActionHandlerFlowableTest {

    @InjectMocks
    private InstallationOnsiteInspectionAddSubmittedRequestActionHandlerFlowable handler;

    @Mock
    private InstallationOnsiteInspectionAddSubmittedRequestActionService service;

    @Mock
    private DelegateExecution execution;


    @Test
    void execute(){
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(service, times(1)).add(requestId);
    }
}
