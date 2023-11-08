package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaChangeType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRegulatorLedApprovedCustomRequestActionMapperTest {
	
	@InjectMocks
    private EmpVariationCorsiaRegulatorLedApprovedCustomRequestActionMapper cut;

    @Test
    void getUserRoleTypes() {
    	assertThat(cut.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.VERIFIER, RoleType.OPERATOR);
    }
    
    @Test
    void getRequestActionType() {
    	assertThat(cut.getRequestActionType()).isEqualTo(RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED);
    }
    
    @Test
    void toRequestActionDTO() {
		EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload requestActionPayload = 
			EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload
				.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("detailsReason")
						.changes(List.of(EmpVariationCorsiaChangeType.NEW_FUEL_TYPE))
						.build())
				.reviewGroupDecisions(Map.of(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
						.notes("notes")
						.variationScheduleItems(List.of("item1", "item2"))
						.build()))
				.decisionNotification(DecisionNotification.builder()
						.operators(Set.of("op1", "op2"))
						.build())
				.build();
		
		RequestAction requestAction = RequestAction.builder()
				.id(1L)
				.payload(requestActionPayload)
				.build();
		
		RequestActionDTO result = cut.toRequestActionDTO(requestAction);
		
		assertThat(result.getId()).isEqualTo(requestAction.getId());
		EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload resultPayload = 
			(EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload) result.getPayload();
		assertThat(resultPayload.getEmissionsMonitoringPlan()).isEqualTo(requestActionPayload.getEmissionsMonitoringPlan());
		assertThat(resultPayload.getReviewGroupDecisions()).containsExactlyEntriesOf(Map.of(
				EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
				.variationScheduleItems(List.of("item1", "item2"))
				.build()
				));
    }
    
}
