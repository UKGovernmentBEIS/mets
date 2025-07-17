package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler.InstallationAuditSubmitNotifyOperatorActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.InstallationInspectionSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionSubmitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationInspectionSubmitNotifyOperatorActionHandlerTest {

    @InjectMocks
    private InstallationAuditSubmitNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationAuditSubmitService installationAuditSubmitService;

    @Mock
    private InstallationOnsiteInspectionSubmitService installationOnsiteInspectionSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Spy
    private ArrayList<InstallationInspectionSubmitService> installationInspectionSubmitServices;

    @BeforeEach
    public void setUp() {

        when(installationOnsiteInspectionSubmitService.getRequestType()).thenReturn(RequestType.INSTALLATION_ONSITE_INSPECTION);
        when(installationAuditSubmitService.getRequestType()).thenReturn(RequestType.INSTALLATION_AUDIT);

        installationInspectionSubmitServices.add(installationOnsiteInspectionSubmitService);
        installationInspectionSubmitServices.add(installationAuditSubmitService);
    }

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR;
        AppUser appUser = AppUser.builder().build();
        NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR_PAYLOAD)
                .decisionNotification(DecisionNotification.builder().signatory("sign").build())
                .build();

        Request request = Request.builder().type(RequestType.INSTALLATION_AUDIT).id("2").build();

        UUID att1 = UUID.randomUUID();
        final Map<String, Boolean> installationInspectionSectionsCompleted = new HashMap<>();
        installationInspectionSectionsCompleted.put("followUpActions",false);
        InstallationInspection installationInspection = InstallationInspection.builder().followUpActionsRequired(true).build();

        InstallationAuditApplicationSubmitRequestTaskPayload taskPayload = InstallationAuditApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.INSTALLATION_AUDIT_APPLICATION_SUBMIT_PAYLOAD)
                .installationInspection(installationInspection)
                .inspectionAttachments(Map.of(att1, "atta1.pdf"))
                .installationInspectionSectionsCompleted(installationInspectionSectionsCompleted)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId("processTaskId")
                .payload(taskPayload)
                .request(request)
                .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType,  appUser, payload);

        assertThat(request.getSubmissionDate()).isNotNull();

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(installationAuditSubmitService, times(1)).applySubmitNotify(requestTask, payload.getDecisionNotification(), appUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.INSTALLATION_AUDIT_SUBMIT_OUTCOME, InstallationInspectionSubmitOutcome.SUBMITTED,
                        BpmnProcessConstants.INSTALLATION_INSPECTION_ARE_FOLLOWUP_ACTIONS_REQUIRED, "true"));
    }
}
