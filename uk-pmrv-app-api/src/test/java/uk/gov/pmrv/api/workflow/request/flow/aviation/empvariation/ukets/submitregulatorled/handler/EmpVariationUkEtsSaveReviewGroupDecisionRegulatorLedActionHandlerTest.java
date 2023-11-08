package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

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

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsSubmitRegulatorLedService;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedActionHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedActionHandler cut;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
	private EmpVariationUkEtsSubmitRegulatorLedService empVariationUkEtsSubmitRegulatorLedService;
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REGULATOR_LED;
		PmrvUser pmrvUser = PmrvUser.builder().build();
		EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload payload = EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload.builder()
				.group(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
				.decision(EmpAcceptedVariationDecisionDetails.builder()
						.notes("notes")
						.variationScheduleItems(List.of("var1", "var2"))
						.build())
				.build();
		RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType, pmrvUser, payload);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(empVariationUkEtsSubmitRegulatorLedService, times(1)).saveReviewGroupDecision(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED);
	}
}
