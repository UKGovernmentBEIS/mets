package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowUpSetTaskDueDateHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @InjectMocks
    private FollowUpSetTaskDueDateHandlerFlowable handler;

    @Test
    void execute_convertsExpirationDateToLocalDate_andSetsDueDateToTasks() {
        String requestId = "REQ-123";

        Date expirationDate = new Date(1700000000000L);
        LocalDate expectedLocalDate = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE)).thenReturn(expirationDate);
        handler.execute(execution);

        ArgumentCaptor<LocalDate> localDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(requestTaskTimeManagementService).setDueDateToTasks(
                eq(requestId),
                eq(RequestExpirationType.FOLLOW_UP_RESPONSE),
                localDateCaptor.capture()
        );

        assertEquals(expectedLocalDate, localDateCaptor.getValue());
        verifyNoMoreInteractions(requestTaskTimeManagementService);
    }
}
