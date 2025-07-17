package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermitVariationReviewSaveDeterminationActionHandler 
	implements RequestTaskActionHandler<PermitVariationSaveReviewDeterminationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitVariationReviewService permitVariationReviewService;
    private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final PermitVariationSaveReviewDeterminationRequestTaskActionPayload payload) {
        final PermitVariationDeterminateable determination = payload.getDetermination();
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestType requestType = requestTask.getRequest().getType();
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        if (!permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload, requestType)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        permitVariationReviewService.saveDetermination(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION);
    }
}
