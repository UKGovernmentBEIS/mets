package uk.gov.pmrv.api.workflow.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGeneratorResolver;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service.SystemMessageNotificationRequestSequenceRequestIdGenerator;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;

@ExtendWith(MockitoExtension.class)
class StartProcessRequestServiceTest {

    @InjectMocks
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private RequestCreateService requestCreateService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestIdGeneratorResolver requestIdGeneratorResolver;

    @Mock
    private SystemMessageNotificationRequestSequenceRequestIdGenerator systemMessageNotificationRequestSequenceRequestIdGenerator;

    @Test
    void startProcess() {
        final String requestId = "1";
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestParams params = RequestParams.builder()
                .type(requestType)
                .accountId(1L)
                .processVars(Map.of(
                		"proccVar1", "processVar1Val"
                		))
                .build();
        final String processInstanceId = "prInstanceId";
        
        RequestIdGenerator requestIdGenerator = Mockito.mock(RequestIdGenerator.class);
        when(requestIdGeneratorResolver.get(requestType)).thenReturn(requestIdGenerator);
        when(requestIdGenerator.generate(params)).thenReturn(requestId);
        
        Request request = Request.builder().id(requestId).type(requestType).build();

        when(requestCreateService.createRequest(params.withRequestId(requestId), IN_PROGRESS))
            .thenReturn(request);

        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.REQUEST_ID, requestId);
        processVars.put(BpmnProcessConstants.REQUEST_TYPE, requestType.name());
        processVars.putAll(params.getProcessVars());
        
        when(workflowService.startProcessDefinition(requestType.getProcessDefinitionId(), processVars))
            .thenReturn(processInstanceId);

        // Invoke
        Request result = startProcessRequestService.startProcess(params);

        //assert
        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getProcessInstanceId()).isEqualTo(processInstanceId);
        // Verify
        verify(requestIdGeneratorResolver, times(1)).get(requestType);
        verify(requestIdGenerator, times(1)).generate(params);
        verify(requestCreateService, times(1)).createRequest(params.withRequestId(requestId), IN_PROGRESS);
        verify(workflowService, times(1)).startProcessDefinition(requestType.getProcessDefinitionId(), processVars);
        assertThat(result.getProcessInstanceId()).isEqualTo(processInstanceId);
    }

    @Test
    void startSystemMessageNotificationProcess() {
        final String requestId = "1";
        final String assignee = "userId";
        final RequestType requestType = RequestType.SYSTEM_MESSAGE_NOTIFICATION;
        final RequestTaskType requestTaskType = RequestTaskType.ACCOUNT_USERS_SETUP;
        final SystemMessageNotificationRequestPayload requestPayload = SystemMessageNotificationRequestPayload.builder()
            .operatorAssignee(assignee)
            .build();
        final RequestParams requestParams = RequestParams.builder()
            .type(requestType)
            .accountId(1L)
            .requestPayload(requestPayload)
            .build();
        final String processInstanceId = "prInstanceId";

        Request request = Request.builder().id(requestId).type(requestType).payload(requestPayload).build();

        when(requestCreateService.createRequest(requestParams, IN_PROGRESS))
            .thenReturn(request);

        Map<String, Object> processVars = Map.of(
            BpmnProcessConstants.REQUEST_ID, request.getId(),
            BpmnProcessConstants.REQUEST_TASK_TYPE, requestTaskType,
            BpmnProcessConstants.REQUEST_TASK_ASSIGNEE, assignee
        );

        when(requestIdGeneratorResolver.get(any())).thenReturn(systemMessageNotificationRequestSequenceRequestIdGenerator);
        when(workflowService.startProcessDefinition(requestType.getProcessDefinitionId(), processVars))
            .thenReturn(processInstanceId);


        // Invoke
        Request result =
            startProcessRequestService.startSystemMessageNotificationProcess(requestParams, requestTaskType);

        //assert
        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getProcessInstanceId()).isEqualTo(processInstanceId);
        // Verify
        verify(requestCreateService, times(1)).createRequest(requestParams, IN_PROGRESS);
        verify(workflowService, times(1)).startProcessDefinition(requestType.getProcessDefinitionId(), processVars);
        assertThat(result.getProcessInstanceId()).isEqualTo(processInstanceId);
    }

    @Test
    void reStartProcess() {
        final String requestId = "1";
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final String processInstanceId = "prInstanceId";

        Request request = Request.builder()
                .id(requestId)
                .type(requestType)
                .processInstanceId("previousInstanceId")
                .status(RequestStatus.COMPLETED)
                .build();

        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.REQUEST_ID, request.getId());
        processVars.put(BpmnProcessConstants.REQUEST_TYPE, request.getType().name());

        when(workflowService.startProcessDefinition(requestType.getProcessDefinitionId(), processVars))
                .thenReturn(processInstanceId);

        // Invoke
        startProcessRequestService.reStartProcess(request);

        // Verify
        verify(workflowService, times(1)).startProcessDefinition(requestType.getProcessDefinitionId(), processVars);
        assertThat(request.getProcessInstanceId()).isEqualTo(processInstanceId);
        assertThat(request.getStatus()).isEqualTo(IN_PROGRESS);
    }
}
