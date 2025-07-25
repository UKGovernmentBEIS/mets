package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.mapper.PermitReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation.PermitReviewReturnForAmendsValidatorService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitReviewReturnForAmendsHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final PermitIssuanceReviewService permitIssuanceReviewService;
    private final PermitReviewReturnForAmendsValidatorService permitReviewReturnForAmendsValidatorService;
    private final WorkflowService workflowService;
    private static final PermitReviewMapper permitReviewMapper = Mappers.getMapper(PermitReviewMapper.class);

    @Override
    @Transactional
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate that at least one review group is 'Operator to amend'
        permitReviewReturnForAmendsValidatorService.validate((PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Update request payload
        permitIssuanceReviewService.saveRequestReturnForAmends(requestTask, appUser);

        // Add PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS request action
        createRequestAction(requestTask.getRequest(), appUser, (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Close task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(Request request, AppUser appUser, PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {
        PermitIssuanceApplicationReturnedForAmendsRequestActionPayload requestActionPayload = permitReviewMapper
                .toPermitIssuanceApplicationReturnedForAmendsRequestActionPayload(
                    taskPayload, 
                    RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }
}
