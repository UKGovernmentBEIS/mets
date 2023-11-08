package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.service.EmpVariationCorsiaSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@RequiredArgsConstructor
@Component
public class EmpVariationCorsiaSaveActionHandler implements RequestTaskActionHandler<EmpVariationCorsiaSaveApplicationRequestTaskActionPayload>{

	private final RequestTaskService requestTaskService;
	private final EmpVariationCorsiaSubmitService empVariationCorsiaSubmitService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
			EmpVariationCorsiaSaveApplicationRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		empVariationCorsiaSubmitService.saveEmpVariation(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION);
	}
}
