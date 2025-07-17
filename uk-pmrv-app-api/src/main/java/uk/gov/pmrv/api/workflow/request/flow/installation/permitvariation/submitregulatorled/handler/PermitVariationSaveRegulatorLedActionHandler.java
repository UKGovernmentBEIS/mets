package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationSubmitRegulatorLedService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PermitVariationSaveRegulatorLedActionHandler
		implements RequestTaskActionHandler<PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
	private final PermitVariationSubmitRegulatorLedService permitVariationSubmitRegulatorLedService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
			PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		permitVariationSubmitRegulatorLedService.savePermitVariation(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_REGULATOR_LED);
	}

}
