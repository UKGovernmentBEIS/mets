package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaApplicationSubmitInitializerTest {

	@InjectMocks
    private EmpVariationCorsiaApplicationSubmitInitializer handler;
	
	@Mock
    private EmissionsMonitoringPlanQueryService empQueryService;
	
	@Mock
	private RequestAviationAccountQueryService aviationAccountQueryService;
	
	@Test
	void initializePayload() {
		Long accountId = 1L;
		Request request = Request.builder().accountId(accountId).build();
		
		EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia
				.builder()
				.abbreviations(EmpAbbreviations.builder().exist(false).build())
				.operatorDetails(EmpCorsiaOperatorDetails.builder()
						.activitiesDescription(ActivitiesDescriptionCorsia.builder()
								.activityDescription("actDescr")
								.build())
						.operatorName("opName")
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
				.operatorName("opName2")
				.serviceContactDetails(ServiceContactDetails.builder().name("name").build())
				.build();
		
		when(empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)).thenReturn(Optional.of(empDto));
		when(aviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		RequestTaskPayload result = handler.initializePayload(request);
		
		verify(empQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
		verify(aviationAccountQueryService, times(1)).getAccountInfo(accountId);
				
		EmpVariationCorsiaApplicationSubmitRequestTaskPayload payload = (EmpVariationCorsiaApplicationSubmitRequestTaskPayload) result;
		assertThat(payload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_PAYLOAD);
		assertThat(payload.getServiceContactDetails().getName()).isEqualTo("name");
		assertThat(payload.getEmissionsMonitoringPlan().getAbbreviations()).isEqualTo(empContainer.getEmissionsMonitoringPlan().getAbbreviations());
		assertThat(payload.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName()).isEqualTo("opName2");
		assertThat(payload.getEmpAttachments()).containsAllEntriesOf(Map.of(att1, "Att1.pdf"));
	}
	
	@Test
	void getRequestTaskTypes() {
		assertThat(handler.getRequestTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT
				);
	}
}
