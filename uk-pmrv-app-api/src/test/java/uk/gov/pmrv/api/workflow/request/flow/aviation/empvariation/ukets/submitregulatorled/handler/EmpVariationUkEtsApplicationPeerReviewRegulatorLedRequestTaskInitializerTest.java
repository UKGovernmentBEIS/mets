package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsApplicationPeerReviewRegulatorLedRequestTaskInitializerTest {

	@InjectMocks
    private EmpVariationUkEtsApplicationPeerReviewRegulatorLedRequestTaskInitializer cut;
	
	@Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Test
	void getRequestTaskTypes() {
		assertThat(cut.getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW);
	}
	
	@Test
	void initializePayload() {
		EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(false)
								.build())
						.operatorDetails(EmpOperatorDetails.builder().build())
						.build())
				.empVariationDetails(EmpVariationUkEtsDetails.builder()
						.reason("reason")
						.build())
				.build();
		
		Long accountId = 1L;
		Request request = Request.builder()
				.payload(requestPayload)
				.accountId(accountId)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
				.crcoCode("crcoCode")
				.build();
		
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		RequestTaskPayload result = cut.initializePayload(request);
		assertThat(result).isInstanceOf(EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.class);
		assertThat(result).isEqualTo(EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD)
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(false)
								.build())
						.operatorDetails(EmpOperatorDetails.builder().crcoCode("crcoCode").build())
						.build())
				.empVariationDetails(EmpVariationUkEtsDetails.builder()
						.reason("reason")
						.build())
				.build());
		
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
	}
}
