package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator.WithholdingOfAllowancesValidator;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesRequestPeerReviewActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;
    @Mock
    private WithholdingOfAllowancesValidator withholdingOfAllowancesValidator;
    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    @Mock
    private WithholdingOfAllowancesService withholdingOfAllowancesService;
    @Mock
    private RequestService requestService;
    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private WithholdingOfAllowancesRequestPeerReviewActionHandler actionHandler;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW;
        AppUser appUser = new AppUser();
        PeerReviewRequestTaskActionPayload actionPayload = new PeerReviewRequestTaskActionPayload();

        RequestTask requestTask = new RequestTask();
        Request request = new Request();
        WithholdingOfAllowancesApplicationSubmitRequestTaskPayload payload =
            new WithholdingOfAllowancesApplicationSubmitRequestTaskPayload();
        requestTask.setPayload(payload);
        requestTask.setRequest(request);
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(requestTaskId, requestTaskActionType, appUser, actionPayload);

        verify(withholdingOfAllowancesValidator).validate(payload.getWithholdingOfAllowances());
        verify(peerReviewerTaskAssignmentValidator).validate(
            RequestTaskType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW,
            actionPayload.getPeerReviewer(),
            appUser
        );
        verify(withholdingOfAllowancesService).requestPeerReview(
            requestTask,
            actionPayload.getPeerReviewer(),
            appUser.getUserId()
        );
        verify(requestService).addActionToRequest(
            request,
            null,
            RequestActionType.WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_REQUESTED,
            appUser.getUserId()
        );
        verify(workflowService).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_SUBMIT_OUTCOME, WithholdingOfAllowancesSubmitOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> types = actionHandler.getTypes();

        assertNotNull(types);
        assertEquals(1, types.size());
        assertTrue(types.contains(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW));
    }
}
