package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.mapper.PermitVariationAmendSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewReturnForAmendsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitVariationReviewReturnForAmendsHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final PermitVariationReviewService permitVariationReviewService;
    private final PermitVariationReviewReturnForAmendsValidator permitVariationReviewReturnForAmendsValidator;
    private final WorkflowService workflowService;
    private static final PermitVariationAmendSubmitMapper permitAmendSubmitMapper = Mappers.getMapper(PermitVariationAmendSubmitMapper.class);

    @Override
    @Transactional
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        PermitVariationApplicationReviewRequestTaskPayload permitVariationApplicationReviewRequestTaskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        permitVariationReviewReturnForAmendsValidator.validate(permitVariationApplicationReviewRequestTaskPayload);
        permitVariationReviewService.saveRequestReturnForAmends(requestTask, appUser);
        createRequestAction(requestTask.getRequest(), appUser, permitVariationApplicationReviewRequestTaskPayload);
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(Request request, AppUser appUser, PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        PermitVariationApplicationReturnedForAmendsRequestActionPayload requestActionPayload = permitAmendSubmitMapper
                .toPermitVariationApplicationReturnedForAmendsRequestActionPayload(taskPayload);

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }
}
