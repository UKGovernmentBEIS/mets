package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsReviewSaveActionHandler 
	implements RequestTaskActionHandler<EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
	private final EmpVariationUkEtsReviewService empVariationUkEtsReviewService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
			EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		empVariationUkEtsReviewService.saveEmpVariation(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REVIEW);
	}
}
