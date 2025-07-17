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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler.InstallationAuditRequestPeerReviewActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.validation.InstallationAuditRequestPeerReviewValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.InstallationInspectionSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionSubmitService;

import java.util.ArrayList;
import java.util.Map;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationInspectionRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private InstallationAuditRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationAuditRequestPeerReviewValidator installationInspectionRequestPeerReviewValidator;

    @Mock
    private InstallationAuditSubmitService installationAuditSubmitService;

    @Mock
    private InstallationOnsiteInspectionSubmitService installationOnsiteInspectionSubmitService;

    @Mock
    private RequestService requestService;

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
        String requestId = "1";
        Long requestTaskId = 2L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.INSTALLATION_AUDIT_REQUEST_PEER_REVIEW;
        AppUser appUser = AppUser.builder().userId("user").build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.INSTALLATION_AUDIT_REQUEST_PEER_REVIEW_PAYLOAD)
                .peerReviewer("reg2")
                .build();
        Request request = Request.builder().id(requestId).type(RequestType.INSTALLATION_AUDIT).build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId("processTaskId")
                .request(request)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType,  appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(installationInspectionRequestPeerReviewValidator, times(1)).validate(requestTask, taskActionPayload, appUser);
        verify(installationAuditSubmitService, times(1)).requestPeerReview(requestTask, "reg2", appUser);
        verify(requestService, times(1))
                .addActionToRequest(request, null, RequestActionType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW_REQUESTED, appUser.getUserId());

        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.INSTALLATION_AUDIT_SUBMIT_OUTCOME, InstallationInspectionSubmitOutcome.PEER_REVIEW_REQUIRED));
    }


}
