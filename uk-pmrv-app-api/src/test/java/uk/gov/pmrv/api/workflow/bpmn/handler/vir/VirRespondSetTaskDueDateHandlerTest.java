package uk.gov.pmrv.api.workflow.bpmn.handler.vir;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirRespondSetTaskDueDateHandlerTest {

    @InjectMocks
    private VirRespondSetTaskDueDateHandler handler;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "AEM-001";
        Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.VIR_EXPIRATION_DATE)).thenReturn(expirationDate);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(requestTaskTimeManagementService, times(1)).setDueDateToTasks(requestId, RequestExpirationType.VIR,
                expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.VIR_EXPIRATION_DATE);
    }
}
