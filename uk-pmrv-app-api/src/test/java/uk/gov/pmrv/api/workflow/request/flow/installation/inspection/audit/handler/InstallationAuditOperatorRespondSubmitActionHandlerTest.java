package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditOperatorRespondService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditOperatorRespondSubmitActionHandlerTest {

    @InjectMocks
    private InstallationAuditOperatorRespondSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private InstallationAuditOperatorRespondService installationAuditOperatorRespondService;

    @Test
    void process() {
        final RequestTaskActionEmptyPayload taskActionPayload =
                RequestTaskActionEmptyPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD)
                        .build();

        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_AUDIT_OPERATOR_RESPOND_SUBMIT, appUser, taskActionPayload);

        verify(installationAuditOperatorRespondService, times(1)).applySubmitAction(requestTask,appUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId());
    }

    @Test
    void getTypes(){
        assertThat(handler.getTypes())
                .containsExactlyInAnyOrder(RequestTaskActionType.INSTALLATION_AUDIT_OPERATOR_RESPOND_SUBMIT);
    }
}
