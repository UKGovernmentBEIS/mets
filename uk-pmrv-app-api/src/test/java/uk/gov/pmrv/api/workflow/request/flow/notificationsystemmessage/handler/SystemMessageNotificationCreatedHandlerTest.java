package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

@ExtendWith(MockitoExtension.class)
class SystemMessageNotificationCreatedHandlerTest {

    @InjectMocks
    private SystemMessageNotificationCreatedHandler handler;

    @Mock
    private RequestTaskCreateService requestTaskCreateService;

    @Test
    void create() {
        final String requestId = "1";
        final String processTaskId = "processTaskId";
        final Map<String, Object> variables =
            Map.of(BpmnProcessConstants.REQUEST_TASK_TYPE, RequestTaskType.ACCOUNT_USERS_SETUP,
                   BpmnProcessConstants.REQUEST_TASK_ASSIGNEE, "assignee");

        handler.createRequestTask(requestId, processTaskId, DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.name(), variables);

        verify(requestTaskCreateService, times(1))
            .create(requestId, processTaskId, RequestTaskType.ACCOUNT_USERS_SETUP, "assignee");
    }
    
    @Test
    void getTaskDefinition() {
        assertThat(handler.getTaskDefinitionKey()).isEqualTo(DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.name());
    }

}
