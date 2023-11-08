package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
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
class EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskInitializerTest {

	@InjectMocks
    private EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskInitializer cut;
	
	@Mock
    private EmissionsMonitoringPlanQueryService empQueryService;
	
	@Mock
	private RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Test
	void initializePayload_not_already_determined() {
		
		Long accountId = 1L;
		EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder().build();
		
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		
		EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia
				.builder()
				.abbreviations(EmpAbbreviations.builder().exist(false).build())
				.operatorDetails(EmpCorsiaOperatorDetails.builder()
						.activitiesDescription(ActivitiesDescriptionCorsia.builder()
								.activityDescription("actDescr")
								.build())
						.operatorName("name1")
						.build())
				.build();

		UUID att1 = UUID.randomUUID();
		EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
				.emissionsMonitoringPlan(emp)
				.empAttachments(Map.of(att1, "Att1.pdf"))
				.build();
		EmissionsMonitoringPlanCorsiaDTO empDto = EmissionsMonitoringPlanCorsiaDTO
				.builder()
				.empContainer(empContainer)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo
				.builder()
				.crcoCode("crco2")
				.operatorName("name2")
				.serviceContactDetails(ServiceContactDetails.builder().name("name2").build())
				.build();
		
		when(empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)).thenReturn(Optional.of(empDto));
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		RequestTaskPayload result = cut.initializePayload(request);
		
		verify(empQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
		
		assertThat(result).isEqualTo(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD)
				.originalEmpContainer(empContainer)
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia
					.builder()
					.abbreviations(EmpAbbreviations.builder().exist(false).build())
					.operatorDetails(EmpCorsiaOperatorDetails.builder()
							.activitiesDescription(ActivitiesDescriptionCorsia.builder()
									.activityDescription("actDescr")
									.build())
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
		EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(false)
								.build())
						.operatorDetails(EmpCorsiaOperatorDetails.builder().operatorName("opnamee").build())
						.build())
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("reason")
						.build())
				.reasonRegulatorLed("reasonRegulatorLed")
				.originalEmpContainer(EmissionsMonitoringPlanCorsiaContainer.builder()
						.scheme(EmissionTradingScheme.CORSIA)
						.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
								.abbreviations(EmpAbbreviations.builder()
										.exist(true)
										.build())
								.build())
						.build())
				.build();
		
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		
		EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia
				.builder()
				.abbreviations(EmpAbbreviations.builder().exist(false).build())
				.operatorDetails(EmpCorsiaOperatorDetails.builder()
						.activitiesDescription(ActivitiesDescriptionCorsia.builder()
								.activityDescription("actDescr")
								.build())
						.operatorName("name1")
						.build())
				.build();

		UUID att1 = UUID.randomUUID();
		EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
				.emissionsMonitoringPlan(emp)
				.empAttachments(Map.of(att1, "Att1.pdf"))
				.build();
		EmissionsMonitoringPlanCorsiaDTO empDto = EmissionsMonitoringPlanCorsiaDTO
				.builder()
				.empContainer(empContainer)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo
				.builder()
				.crcoCode("crco2")
				.operatorName("name2")
				.serviceContactDetails(ServiceContactDetails.builder().name("name2").build())
				.build();
		
		when(empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)).thenReturn(Optional.of(empDto));
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		RequestTaskPayload result = cut.initializePayload(request);
		
		verify(empQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
		
		assertThat(((EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload)result).getEmissionsMonitoringPlan()).isEqualTo(EmissionsMonitoringPlanCorsia
				.builder()
				.abbreviations(EmpAbbreviations.builder()
						.exist(false)
						.build())
				.operatorDetails(EmpCorsiaOperatorDetails.builder()
						.operatorName("opnamee")
						.build())
				.build());
		assertThat(((EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload)result).getOriginalEmpContainer()).isEqualTo(EmissionsMonitoringPlanCorsiaContainer.builder()
				.scheme(EmissionTradingScheme.CORSIA)
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.build());
		assertThat(((EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload)result).getEmpVariationDetails()).isEqualTo(
				EmpVariationCorsiaDetails.builder()
				.reason("reason")
				.build()
				);
		assertThat(((EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload)result).getReasonRegulatorLed()).isEqualTo("reasonRegulatorLed");
		assertThat(((EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload)result).getServiceContactDetails()).isEqualTo(
				accountInfo.getServiceContactDetails()
				);
		assertThat(result.getPayloadType()).isEqualTo(
				RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD
				);
		
	}
	
	@Test
	void getRequestTaskTypes() {
		assertThat(cut.getRequestTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT
				);
	}
}

