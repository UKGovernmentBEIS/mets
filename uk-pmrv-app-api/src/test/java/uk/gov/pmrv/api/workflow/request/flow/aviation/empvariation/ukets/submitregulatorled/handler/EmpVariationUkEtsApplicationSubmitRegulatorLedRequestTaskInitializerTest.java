package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReasonType;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskInitializerTest {

	@InjectMocks
    private EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskInitializer cut;
	
	@Mock
    private EmissionsMonitoringPlanQueryService empQueryService;
	
	@Mock
	private RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Test
	void initializePayload_not_already_determined() {
		Long accountId = 1L;
		EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
				.build();
		
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		
		EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts
				.builder()
				.abbreviations(EmpAbbreviations.builder().exist(false).build())
				.operatorDetails(EmpOperatorDetails.builder()
						.activitiesDescription(ActivitiesDescription.builder()
								.activityDescription("actDescr")
								.build())
						.crcoCode("crco1")
						.operatorName("name1")
						.build())
				.build();

		UUID att1 = UUID.randomUUID();
		EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
				.emissionsMonitoringPlan(emp)
				.empAttachments(Map.of(att1, "Att1.pdf"))
				.build();
		EmissionsMonitoringPlanUkEtsDTO empDto = EmissionsMonitoringPlanUkEtsDTO
				.builder()
				.empContainer(empContainer)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo
				.builder()
				.crcoCode("crco2")
				.operatorName("name2")
				.serviceContactDetails(ServiceContactDetails.builder().name("name2").build())
				.build();
		
		when(empQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(empDto));
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		RequestTaskPayload result = cut.initializePayload(request);
		
		verify(empQueryService, times(1)).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
		
		assertThat(result).isEqualTo(EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD)
				.originalEmpContainer(empContainer)
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts
					.builder()
					.abbreviations(EmpAbbreviations.builder().exist(false).build())
					.operatorDetails(EmpOperatorDetails.builder()
							.activitiesDescription(ActivitiesDescription.builder()
									.activityDescription("actDescr")
									.build())
							.crcoCode("crco2")
							.operatorName("name2")
							.build())
					.build())
				.serviceContactDetails(accountInfo.getServiceContactDetails())
				.empAttachments(Map.of(att1, "Att1.pdf"))
				.build());
	}
	
	@Test
	void initializePayload_already_determined() {
		Long accountId = 1L;
		EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(false)
								.build())
						.operatorDetails(EmpOperatorDetails.builder().operatorName("opnamee").build())
						.build())
				.empVariationDetails(EmpVariationUkEtsDetails.builder()
						.reason("reason")
						.build())
				.reasonRegulatorLed(EmpVariationUkEtsRegulatorLedReason.builder()
						.type(EmpVariationUkEtsRegulatorLedReasonType.FOLLOWING_IMPROVING_REPORT)
						.build())
				.originalEmpContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
						.scheme(EmissionTradingScheme.UK_ETS_AVIATION)
						.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
								.abbreviations(EmpAbbreviations.builder()
										.exist(true)
										.build())
								.build())
						.build())
				.build();
		
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		
		EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts
				.builder()
				.abbreviations(EmpAbbreviations.builder().exist(false).build())
				.operatorDetails(EmpOperatorDetails.builder()
						.activitiesDescription(ActivitiesDescription.builder()
								.activityDescription("actDescr")
								.build())
						.crcoCode("crco1")
						.operatorName("name1")
						.build())
				.build();

		UUID att1 = UUID.randomUUID();
		EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
				.emissionsMonitoringPlan(emp)
				.empAttachments(Map.of(att1, "Att1.pdf"))
				.build();
		EmissionsMonitoringPlanUkEtsDTO empDto = EmissionsMonitoringPlanUkEtsDTO
				.builder()
				.empContainer(empContainer)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo
				.builder()
				.crcoCode("crco2")
				.operatorName("name2")
				.serviceContactDetails(ServiceContactDetails.builder().name("name2").build())
				.build();
		
		when(empQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(empDto));
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		RequestTaskPayload result = cut.initializePayload(request);
		
		verify(empQueryService, times(1)).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
		
		assertThat(((EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload)result).getEmissionsMonitoringPlan()).isEqualTo(EmissionsMonitoringPlanUkEts
				.builder()
				.abbreviations(EmpAbbreviations.builder()
						.exist(false)
						.build())
				.operatorDetails(EmpOperatorDetails.builder()
						.crcoCode("crco2")
						.operatorName("opnamee")
						.build())
				.build());
		assertThat(((EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload)result).getOriginalEmpContainer()).isEqualTo(EmissionsMonitoringPlanUkEtsContainer.builder()
				.scheme(EmissionTradingScheme.UK_ETS_AVIATION)
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.build());
		assertThat(((EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload)result).getEmpVariationDetails()).isEqualTo(
				EmpVariationUkEtsDetails.builder()
				.reason("reason")
				.build()
				);
		assertThat(((EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload)result).getReasonRegulatorLed()).isEqualTo(
				EmpVariationUkEtsRegulatorLedReason.builder()
				.type(EmpVariationUkEtsRegulatorLedReasonType.FOLLOWING_IMPROVING_REPORT)
				.build()
				);
		assertThat(((EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload)result).getServiceContactDetails()).isEqualTo(
				accountInfo.getServiceContactDetails()
				);
		assertThat(((EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload)result).getPayloadType()).isEqualTo(
				RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD
				);
		
	}
	
	@Test
	void getRequestTaskTypes() {
		assertThat(cut.getRequestTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT
				);
	}
}

