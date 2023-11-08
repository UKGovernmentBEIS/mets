package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsApplicationSubmitInitializerTest {

	@InjectMocks
    private EmpVariationUkEtsApplicationSubmitInitializer handler;
	
	@Mock
    private EmissionsMonitoringPlanQueryService empQueryService;
	
	@Mock
	private RequestAviationAccountQueryService aviationAccountQueryService;
	
	@Test
	void initializePayload() {
		Long accountId = 1L;
		Request request = Request.builder().accountId(accountId).build();
		
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
		when(aviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		RequestTaskPayload result = handler.initializePayload(request);
		
		verify(empQueryService, times(1)).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
		verify(aviationAccountQueryService, times(1)).getAccountInfo(accountId);
				
		EmpVariationUkEtsApplicationSubmitRequestTaskPayload payload = (EmpVariationUkEtsApplicationSubmitRequestTaskPayload) result;
		assertThat(payload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_PAYLOAD);
		assertThat(payload.getServiceContactDetails().getName()).isEqualTo("name2");
		assertThat(payload.getEmissionsMonitoringPlan().getAbbreviations()).isEqualTo(empContainer.getEmissionsMonitoringPlan().getAbbreviations());
		assertThat(payload.getEmissionsMonitoringPlan().getOperatorDetails().getCrcoCode()).isEqualTo("crco2");
		assertThat(payload.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName()).isEqualTo("name2");
		assertThat(payload.getEmpAttachments()).containsAllEntriesOf(Map.of(att1, "Att1.pdf"));
	}
	
	@Test
	void getRequestTaskTypes() {
		assertThat(handler.getRequestTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT
				);
	}
}
