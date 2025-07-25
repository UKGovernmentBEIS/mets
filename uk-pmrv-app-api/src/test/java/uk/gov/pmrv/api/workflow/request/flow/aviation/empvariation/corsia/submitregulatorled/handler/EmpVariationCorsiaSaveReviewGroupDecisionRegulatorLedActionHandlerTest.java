package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaSubmitRegulatorLedService;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedActionHandlerTest {

	@InjectMocks
    private EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedActionHandler cut;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
	private EmpVariationCorsiaSubmitRegulatorLedService regulatorLedService;
	
	@Test
	void process() {
		
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_REGULATOR_LED;
		AppUser appUser = AppUser.builder().build();
		EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload payload = EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload.builder()
				.group(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
				.decision(EmpAcceptedVariationDecisionDetails.builder()
						.notes("notes")
						.variationScheduleItems(List.of("var1", "var2"))
						.build())
				.build();
		RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType, appUser, payload);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(regulatorLedService, times(1)).saveReviewGroupDecision(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED);
	}
}
