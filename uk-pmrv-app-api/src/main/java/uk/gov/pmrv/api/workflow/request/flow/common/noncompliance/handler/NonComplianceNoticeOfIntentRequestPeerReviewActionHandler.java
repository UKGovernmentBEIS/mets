package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceNoticeOfIntentApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation.NonCompliancePeerReviewValidator;

@Component
@RequiredArgsConstructor
public class NonComplianceNoticeOfIntentRequestPeerReviewActionHandler
    implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NonCompliancePeerReviewValidator nonCompliancePeerReviewValidator;
    private final NonComplianceNoticeOfIntentApplyService applyService;
    private final RequestService requestService;
    private final WorkflowService workflowService;


    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PeerReviewRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NonComplianceNoticeOfIntentRequestTaskPayload taskPayload =
            (NonComplianceNoticeOfIntentRequestTaskPayload) requestTask.getPayload();

        nonCompliancePeerReviewValidator.validateNoticeOfIntentPeerReview(
            taskPayload,
            RequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW,
            payload, 
            pmrvUser
        );

        applyService.saveRequestPeerReviewAction(requestTask, payload.getPeerReviewer());

        requestService.addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED,
            pmrvUser.getUserId()
        );

        final String context = requestTask.getRequest().getType() == RequestType.NON_COMPLIANCE ?
            RequestCustomContext.NON_COMPLIANCE_NOTICE_OF_INTENT.getCode() :
            RequestCustomContext.AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT.getCode();

        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.PEER_REVIEW_REQUIRED,
                BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, context
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW);
    }
}
