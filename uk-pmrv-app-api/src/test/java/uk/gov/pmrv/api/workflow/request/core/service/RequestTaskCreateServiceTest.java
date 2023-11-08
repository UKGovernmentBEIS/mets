package uk.gov.pmrv.api.workflow.request.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.ArrayList;
import java.util.Set;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTaskCreateServiceTest {

    private static final String REQUEST_ID = "1";
    private static final String PROCESS_INSTANCE_ID = "process_instance_id";
    private static final String PROCESS_TASK_ID = "process_task_id";

    @InjectMocks
    private RequestTaskCreateService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskDefaultAssignmentService requestTaskDefaultAssignmentService;

    @Mock
    private TestInitializeRequestTaskHandler initializeRequestTaskHandler;

    @Spy
    private ArrayList<InitializeRequestTaskHandler> initializeRequestTaskHandlers;

    @BeforeEach
    public void setUp() {
        initializeRequestTaskHandlers.add(initializeRequestTaskHandler);
    }
    
    @Test
    void create_assign_to_user_provided() {
        final RequestTaskType type = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
        String userToAssignTask = "assignee";
        Request request = createRequest();
        RequestTaskPayload requestTaskPayload = Mockito.mock(RequestTaskPayload.class);
        
        when(requestService.findRequestById(REQUEST_ID)).thenReturn(request);
        when(initializeRequestTaskHandler.getRequestTaskTypes()).thenReturn(Set.of(type));
        when(initializeRequestTaskHandler.initializePayload(request)).thenReturn(requestTaskPayload);

        //invoke
        service.create(REQUEST_ID, PROCESS_TASK_ID, type, userToAssignTask);

        //verify
        verify(requestService, times(1)).findRequestById(REQUEST_ID);
        verify(requestTaskDefaultAssignmentService, never()).assignDefaultAssigneeToTask(Mockito.any());
    }
    
    @Test
    void create_assign_to_default() {
        final RequestTaskType type = RequestTaskType.ACCOUNT_USERS_SETUP;
        Request request = createRequest();
        RequestTask requestTask = createRequestTask(request, PROCESS_TASK_ID, type.name());

        when(requestService.findRequestById(REQUEST_ID)).thenReturn(request);

        //invoke
        service.create(REQUEST_ID, PROCESS_TASK_ID, type);

        //verify
        verify(requestService, times(1)).findRequestById(REQUEST_ID);
        verify(requestTaskDefaultAssignmentService, times(1)).assignDefaultAssigneeToTask(requestTask);
    }
    
    @Test
    void create_assign_to_default_with_expiration_date_key() {
        final RequestTaskType type = RequestTaskType.ACCOUNT_USERS_SETUP;
        Request request = createRequest();
        RequestTask requestTask = createRequestTask(request, PROCESS_TASK_ID, type.name());

        when(requestService.findRequestById(REQUEST_ID)).thenReturn(request);

        //invoke
        service.create(REQUEST_ID, PROCESS_TASK_ID, type, null, null);

        //verify
        verify(requestService, times(1)).findRequestById(REQUEST_ID);
        verify(requestTaskDefaultAssignmentService, times(1)).assignDefaultAssigneeToTask(requestTask);
    }

    private Request createRequest() {
        Request request = new Request();
        request.setId(REQUEST_ID);
        request.setProcessInstanceId(PROCESS_INSTANCE_ID);
        return request;
    }

    private RequestTask createRequestTask(Request request, String processTaskId, String taskDefinitionKey) {
        return RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .type(RequestTaskType.valueOf(taskDefinitionKey))
                .build();
    }

    private static class TestInitializeRequestTaskHandler implements InitializeRequestTaskHandler {
        @Override
        public RequestTaskPayload initializePayload(Request request) {
            return null;
        }

        @Override
        public Set<RequestTaskType> getRequestTaskTypes() {
            return null;
        }
    }

}
