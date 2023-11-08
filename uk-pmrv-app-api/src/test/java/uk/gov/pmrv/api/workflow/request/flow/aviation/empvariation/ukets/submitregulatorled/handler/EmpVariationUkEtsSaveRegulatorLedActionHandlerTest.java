package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

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

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsSubmitRegulatorLedService;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsSaveRegulatorLedActionHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsSaveRegulatorLedActionHandler cut;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
	private EmpVariationUkEtsSubmitRegulatorLedService empVariationUkEtsSubmitRegulatorLedService;
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REGULATOR_LED;
		PmrvUser pmrvUser = PmrvUser.builder().build();
		EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload payload = EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.empSectionsCompleted(Map.of("ABBREV", List.of(Boolean.FALSE)))
				.build();
		RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType, pmrvUser, payload);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(empVariationUkEtsSubmitRegulatorLedService, times(1)).saveEmpVariation(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REGULATOR_LED);
	}
}
	
