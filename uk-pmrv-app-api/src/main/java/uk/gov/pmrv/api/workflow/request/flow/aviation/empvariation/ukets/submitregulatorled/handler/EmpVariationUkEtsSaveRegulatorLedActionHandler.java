package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsSubmitRegulatorLedService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@RequiredArgsConstructor
@Component
public class EmpVariationUkEtsSaveRegulatorLedActionHandler
		implements RequestTaskActionHandler<EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
	private final EmpVariationUkEtsSubmitRegulatorLedService empVariationUkEtsSubmitRegulatorLedService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
			EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		empVariationUkEtsSubmitRegulatorLedService.saveEmpVariation(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REGULATOR_LED);
	}
}