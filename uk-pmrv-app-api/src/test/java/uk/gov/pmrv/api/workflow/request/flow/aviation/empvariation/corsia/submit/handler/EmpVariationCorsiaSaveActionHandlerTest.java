package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.service.EmpVariationCorsiaSubmitService;


@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaSaveActionHandlerTest {

	@InjectMocks
    private EmpVariationCorsiaSaveActionHandler handler;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
    private EmpVariationCorsiaSubmitService service;
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION;
		PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		EmpVariationCorsiaSaveApplicationRequestTaskActionPayload payload = 
				EmpVariationCorsiaSaveApplicationRequestTaskActionPayload
				.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia
						.builder()
						.abbreviations(EmpAbbreviations.builder().exist(false).build())
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder().id(1L).build();
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		handler.process(requestTaskId, requestTaskActionType, pmrvUser, payload);
		
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(service, times(1)).saveEmpVariation(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(handler.getTypes())
				.containsExactlyInAnyOrder(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION);
	}
}
