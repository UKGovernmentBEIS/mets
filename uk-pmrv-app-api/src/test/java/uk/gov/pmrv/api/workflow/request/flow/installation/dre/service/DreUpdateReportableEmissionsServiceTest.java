package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsSaveParams;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableEmission;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.CalculationOfPFCReportingEmissions;

@ExtendWith(MockitoExtension.class)
class DreUpdateReportableEmissionsServiceTest {

	@InjectMocks
    private DreUpdateReportableEmissionsService cut;

    @Mock
    private RequestService requestService;
    
    @Mock
    private ReportableEmissionsService reportableEmissionsService;
    
    @Test
    void updateReportableEmissions() {
    	String requestId = "1";
    	Request request = Request.builder()
    			.accountId(1L)
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
    			.metadata(DreRequestMetadata.builder().year(Year.of(2022)).build())
    			.build();
    	
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	
    	cut.updateReportableEmissions(requestId);
    	
    	verify(requestService, times(1)).findRequestById(requestId);
    	
    	ArgumentCaptor<ReportableEmissionsSaveParams> paramsCaptor = ArgumentCaptor.forClass(ReportableEmissionsSaveParams.class);
        verify(reportableEmissionsService, times(1)).saveReportableEmissions(paramsCaptor.capture());

        ReportableEmissionsSaveParams paramsCaptured = paramsCaptor.getValue();
        assertThat(paramsCaptured).isEqualTo(ReportableEmissionsSaveParams.builder()
                .accountId(request.getAccountId())
                .year(Year.of(2022))
                .reportableEmissions(((DreRequestPayload)request.getPayload()).getDre().getTotalReportableEmissions())
                .isFromDre(true)
                .build());
    }
}
