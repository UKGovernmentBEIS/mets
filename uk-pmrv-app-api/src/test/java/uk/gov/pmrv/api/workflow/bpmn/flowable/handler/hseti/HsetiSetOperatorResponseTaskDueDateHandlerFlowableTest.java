package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.hseti;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HsetiSetOperatorResponseTaskDueDateHandlerFlowableTest {

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private HsetiSetOperatorResponseTaskDueDateHandlerFlowable handler;

    @Test
    void execute_shouldSetDueDateToTasks() {
        Date expirationDate = new Date(1700000000000L);
        LocalDate expected = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn("REQ-1");
        when(execution.getVariable(BpmnProcessConstants.HSE_TI_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution).getVariable(BpmnProcessConstants.HSE_TI_EXPIRATION_DATE);
        verify(requestTaskTimeManagementService).setDueDateToTasks("REQ-1", RequestExpirationType.HSETI, expected);
        verifyNoMoreInteractions(requestTaskTimeManagementService);
    }
}
