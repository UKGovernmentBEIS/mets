package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaPopulateRequestMetadataService {

	private final RequestService requestService;
	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	
	@Transactional
	public void populateRequestMetadata(final String requestId) {
		
		final Request request = requestService.findRequestById(requestId);
		final EmpVariationRequestMetadata requestMetadata = (EmpVariationRequestMetadata) request.getMetadata();
		final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
		
		final int consolidationNumber = 
			emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(request.getAccountId());
		requestMetadata.setEmpConsolidationNumber(consolidationNumber);
		requestPayload.setEmpConsolidationNumber(consolidationNumber);
	}
}
