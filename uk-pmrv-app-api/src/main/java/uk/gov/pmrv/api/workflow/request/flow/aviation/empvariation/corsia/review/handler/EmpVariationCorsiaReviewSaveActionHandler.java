package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.EmpVariationCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Component
@RequiredArgsConstructor
public class EmpVariationCorsiaReviewSaveActionHandler 
	implements RequestTaskActionHandler<EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
	private final EmpVariationCorsiaReviewService empVariationCorsiaReviewService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
			EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		empVariationCorsiaReviewService.saveEmpVariation(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_REVIEW);
	}
}
