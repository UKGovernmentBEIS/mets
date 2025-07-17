package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsChangeType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsRegulatorLedApprovedCustomRequestActionMapperTest {
	
	@InjectMocks
    private EmpVariationUkEtsRegulatorLedApprovedCustomRequestActionMapper cut;

    @Test
    void getUserRoleTypes() {
    	assertThat(cut.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.VERIFIER, RoleTypeConstants.OPERATOR);
    }
    
    @Test
    void getRequestActionType() {
    	assertThat(cut.getRequestActionType()).isEqualTo(RequestActionType.EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED);
    }
    
    @Test
    void toRequestActionDTO() {
		EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload requestActionPayload = EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload
				.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.empVariationDetails(EmpVariationUkEtsDetails.builder()
						.reason("detailsReason")
						.changes(List.of(EmpVariationUkEtsChangeType.DIFFERENT_FUMMS))
						.build())
				.reviewGroupDecisions(Map.of(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
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
		EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload resultPayload = (EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload) result.getPayload();
		assertThat(resultPayload.getEmissionsMonitoringPlan()).isEqualTo(requestActionPayload.getEmissionsMonitoringPlan());
		assertThat(resultPayload.getReviewGroupDecisions()).containsExactlyEntriesOf(Map.of(
				EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
				.variationScheduleItems(List.of("item1", "item2"))
				.build()
				));
    }
    
}
