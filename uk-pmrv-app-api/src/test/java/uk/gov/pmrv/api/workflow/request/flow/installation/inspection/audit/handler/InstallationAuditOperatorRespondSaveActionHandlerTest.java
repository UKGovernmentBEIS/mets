package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditOperatorRespondService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondSaveRequestTaskActionPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditOperatorRespondSaveActionHandlerTest {

    @InjectMocks
    private InstallationAuditOperatorRespondSaveActionHandler handler;

    @Mock
    private InstallationAuditOperatorRespondService installationAuditOperatorRespondService;

    @Mock
    private RequestTaskService requestTaskService;


    @Test
    void process(){
         final InstallationInspectionOperatorRespondSaveRequestTaskActionPayload taskActionPayload =
                InstallationInspectionOperatorRespondSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE_PAYLOAD)
                        .build();

        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE, appUser,
                taskActionPayload);

        verify(installationAuditOperatorRespondService, times(1)).applySaveAction(requestTask, taskActionPayload);
    }

    @Test
    void getTypes(){
        assertThat(handler.getTypes())
                .containsExactlyInAnyOrder(RequestTaskActionType.INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE);
    }
}
