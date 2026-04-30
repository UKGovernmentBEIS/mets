package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import org.flowable.engine.delegate.DelegateExecution;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirSetTaskDueDateHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @InjectMocks
    private AirSetTaskDueDateHandlerFlowable handler;

    @Test
    void execute_callsSetDueDateToTasksWithRequestIdAndExpirationDate() {
        String requestId = "REQ-1";
        Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AIR_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(requestTaskTimeManagementService).setDueDateToTasks(requestId, RequestExpirationType.AIR,
                expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
}
