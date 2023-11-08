package uk.gov.pmrv.api.workflow.bpmn.handler.air;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.util.Date;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

@ExtendWith(MockitoExtension.class)
class AirRespondSetTaskDueDateHandlerTest {

    @InjectMocks
    private AirRespondSetTaskDueDateHandler handler;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {

        String requestId = "AIR-2022-1";
        Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AIR_EXPIRATION_DATE)).thenReturn(expirationDate);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(requestTaskTimeManagementService, times(1)).setDueDateToTasks(requestId, RequestExpirationType.AIR,
            expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.AIR_EXPIRATION_DATE);
    }
}
