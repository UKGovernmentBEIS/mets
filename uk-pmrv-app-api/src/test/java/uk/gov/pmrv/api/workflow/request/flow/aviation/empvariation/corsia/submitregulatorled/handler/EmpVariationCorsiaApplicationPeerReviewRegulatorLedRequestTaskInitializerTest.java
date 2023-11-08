package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaApplicationPeerReviewRegulatorLedRequestTaskInitializerTest {

	@InjectMocks
    private EmpVariationCorsiaApplicationPeerReviewRegulatorLedRequestTaskInitializer cut;
	
	@Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Test
	void getRequestTaskTypes() {
		assertThat(cut.getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW);
	}
	
	@Test
	void initializePayload() {
		EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(false)
								.build())
						.operatorDetails(EmpCorsiaOperatorDetails.builder().build())
						.build())
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("reason")
						.build())
				.build();
		
		Long accountId = 1L;
		Request request = Request.builder()
				.payload(requestPayload)
				.accountId(accountId)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
			.operatorName("operatorName")
				.build();
		
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		RequestTaskPayload result = cut.initializePayload(request);
		assertThat(result).isInstanceOf(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.class)
			.isEqualTo(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD)
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(false)
								.build())
						.operatorDetails(EmpCorsiaOperatorDetails.builder().build())
						.build())
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("reason")
						.build())
				.build());
		
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
	}
}
