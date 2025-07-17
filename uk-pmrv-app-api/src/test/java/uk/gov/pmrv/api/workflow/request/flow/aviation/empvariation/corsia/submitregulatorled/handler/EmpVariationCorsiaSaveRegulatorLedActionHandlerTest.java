package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaSubmitRegulatorLedService;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaSaveRegulatorLedActionHandlerTest {

	@InjectMocks
    private EmpVariationCorsiaSaveRegulatorLedActionHandler cut;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
	private EmpVariationCorsiaSubmitRegulatorLedService regulatorLedService;
	
	@Test
	void process() {
		
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_REGULATOR_LED;
		AppUser appUser = AppUser.builder().build();
		EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload payload = EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.empSectionsCompleted(Map.of("ABBREV", List.of(Boolean.FALSE)))
				.build();
		RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType, appUser, payload);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(regulatorLedService, times(1)).saveEmpVariation(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_REGULATOR_LED);
	}
}
	
