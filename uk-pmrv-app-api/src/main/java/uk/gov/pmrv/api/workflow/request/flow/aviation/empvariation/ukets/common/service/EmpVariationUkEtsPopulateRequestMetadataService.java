package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsPopulateRequestMetadataService {

	private final RequestService requestService;
	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	
	@Transactional
	public void populateRequestMetadata(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final EmpVariationRequestMetadata requestMetadata = (EmpVariationRequestMetadata) request.getMetadata();
		final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
		
		int consolidationNumber = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(request.getAccountId());
		requestMetadata.setEmpConsolidationNumber(consolidationNumber);
		requestPayload.setEmpConsolidationNumber(consolidationNumber);
	}
}
