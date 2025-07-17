package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermitVariationReviewSaveDeterminationRegulatorLedActionHandler implements
		RequestTaskActionHandler<PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload> {
	
	private final RequestTaskService requestTaskService;
    private final PermitVariationRegulatorLedService permitVariationRegulatorLedService;

	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
			PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		permitVariationRegulatorLedService.saveDeterminationRegulatorLed(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED);
	}

}
