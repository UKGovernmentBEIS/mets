package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesService;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator.ReturnOfAllowancesValidator;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesRequestPeerReviewActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;
    @Mock
    private ReturnOfAllowancesValidator returnOfAllowancesValidator;
    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    @Mock
    private ReturnOfAllowancesService returnOfAllowancesService;
    @Mock
    private RequestService requestService;
    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private ReturnOfAllowancesRequestPeerReviewActionHandler actionHandler;

    @Test
    public void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.RETURN_OF_ALLOWANCES_REQUEST_PEER_REVIEW;
        PmrvUser pmrvUser = new PmrvUser();
        PeerReviewRequestTaskActionPayload actionPayload = new PeerReviewRequestTaskActionPayload();

        RequestTask requestTask = new RequestTask();
        Request request = new Request();
        ReturnOfAllowancesApplicationSubmitRequestTaskPayload payload =
            new ReturnOfAllowancesApplicationSubmitRequestTaskPayload();
        requestTask.setPayload(payload);
        requestTask.setRequest(request);
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(requestTaskId, requestTaskActionType, pmrvUser, actionPayload);

        verify(returnOfAllowancesValidator).validate(payload.getReturnOfAllowances());
        verify(returnOfAllowancesService).requestPeerReview(
            requestTask,
            actionPayload.getPeerReviewer(),
            pmrvUser.getUserId()
        );
        verify(peerReviewerTaskAssignmentValidator).validate(
            RequestTaskType.RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW,
            actionPayload.getPeerReviewer(),
            pmrvUser
        );
        verify(requestService).addActionToRequest(
            request,
            null,
            RequestActionType.RETURN_OF_ALLOWANCES_PEER_REVIEW_REQUESTED,
            pmrvUser.getUserId()
        );
        verify(workflowService).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.RETURN_OF_ALLOWANCES_SUBMIT_OUTCOME, ReturnOfAllowancesSubmitOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Test
    public void getTypes() {
        List<RequestTaskActionType> types = actionHandler.getTypes();

        assertNotNull(types);
        assertEquals(1, types.size());
        assertTrue(types.contains(RequestTaskActionType.RETURN_OF_ALLOWANCES_REQUEST_PEER_REVIEW));
    }

}