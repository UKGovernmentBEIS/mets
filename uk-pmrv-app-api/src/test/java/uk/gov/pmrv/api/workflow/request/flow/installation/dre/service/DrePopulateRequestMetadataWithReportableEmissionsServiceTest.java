package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableEmission;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.CalculationOfPFCReportingEmissions;

@ExtendWith(MockitoExtension.class)
class DrePopulateRequestMetadataWithReportableEmissionsServiceTest {

	@InjectMocks
    private DrePopulateRequestMetadataWithReportableEmissionsService cut;

    @Mock
    private RequestService requestService;
    
    @Test
    void updateRequestMetadata() {
    	String requestId = "1";
    	Request request = Request.builder()
    			.payload(DreRequestPayload.builder()
    					.dre(Dre.builder()
    							.monitoringApproachReportingEmissions(Map.of(
    									MonitoringApproachType.CALCULATION_PFC, CalculationOfPFCReportingEmissions.builder()
    									.type(MonitoringApproachType.CALCULATION_PFC)
    									.totalEmissions(ReportableEmission.builder()
    											.reportableEmissions(BigDecimal.TEN)
    											.build())
    									.build()
    									))
    							.build())
    					.build())
    			.metadata(DreRequestMetadata.builder().build())
    			.build();
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	
    	cut.updateRequestMetadata(requestId);
    	
    	verify(requestService, times(1)).findRequestById(requestId);
    	
		assertThat(((DreRequestMetadata) request.getMetadata()).getEmissions())
				.isEqualTo(((DreRequestPayload) request.getPayload()).getDre().getTotalReportableEmissions());
    }
}
