package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermitVariationReviewSaveActionHandler
		implements RequestTaskActionHandler<PermitVariationSaveApplicationReviewRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
	private final PermitVariationReviewService permitVariationReviewService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
			PermitVariationSaveApplicationReviewRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		permitVariationReviewService.savePermitVariation(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_REVIEW);
	}

}
