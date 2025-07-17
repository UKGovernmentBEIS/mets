package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.service.EmpVariationUkEtsSubmitService;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsSaveActionHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsSaveActionHandler handler;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
    private EmpVariationUkEtsSubmitService service;
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION;
		AppUser appUser = AppUser.builder().userId("user").build();
		EmpVariationUkEtsSaveApplicationRequestTaskActionPayload payload = 
				EmpVariationUkEtsSaveApplicationRequestTaskActionPayload
				.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts
						.builder()
						.abbreviations(EmpAbbreviations.builder().exist(false).build())
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder().id(1L).build();
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		handler.process(requestTaskId, requestTaskActionType, appUser, payload);
		
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(service, times(1)).saveEmpVariation(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(handler.getTypes())
				.containsExactlyInAnyOrder(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION);
	}
}
