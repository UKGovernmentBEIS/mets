package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRegulatorLedSubmittedPopulateRequestMetadataServiceTest {

	@InjectMocks
    private EmpVariationCorsiaRegulatorLedSubmittedPopulateRequestMetadataService cut;
	
	@Mock
	private RequestService requestService;
	
	@Mock
	private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	
	@Test
	void populateRequestMetadata() {
		
		String requestId = "1";
		Long accountId = 1L;
		
		EmpVariationRequestMetadata requestMetadata = EmpVariationRequestMetadata.builder()
				.build();
		
		EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
				.build();
		
		Request request = Request.builder()
				.accountId(accountId)
				.metadata(requestMetadata)
				.payload(requestPayload)
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId))
			.thenReturn(1);
		
		cut.populateRequestMetadata(requestId);
		
		assertThat(requestMetadata.getEmpConsolidationNumber()).isEqualTo(1);
		assertThat(requestPayload.getEmpConsolidationNumber()).isEqualTo(1);
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId);
	}
}
