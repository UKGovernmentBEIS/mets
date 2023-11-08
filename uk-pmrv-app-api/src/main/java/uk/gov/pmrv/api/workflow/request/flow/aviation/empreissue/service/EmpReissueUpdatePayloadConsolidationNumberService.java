package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;

@Service
@RequiredArgsConstructor
class EmpReissueUpdatePayloadConsolidationNumberService {

	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	
	@Transactional
	public void updateRequestPayloadConsolidationNumber(Request request) {
		final ReissueRequestPayload requestPayload = (ReissueRequestPayload) request.getPayload();
		int consolidationNumber = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(request.getAccountId());
		requestPayload.setConsolidationNumber(consolidationNumber);
	}
}
