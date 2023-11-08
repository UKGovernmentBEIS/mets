package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsChangeType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReasonType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

class EmpVariationUkEtsSubmitRegulatorLedMapperTest {

	private final EmpVariationUkEtsSubmitRegulatorLedMapper mapper = Mappers.getMapper(EmpVariationUkEtsSubmitRegulatorLedMapper.class);

    @Test
    void toEmpVariationApplicationRegulatorLedApprovedRequestActionPayload() {
    	UUID att1 = UUID.randomUUID();
    	EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
				.abbreviations(EmpAbbreviations.builder().exist(true).build())
				.operatorDetails(EmpOperatorDetails.builder()
						.operatorName("opName1")
						.crcoCode("crcoCode1")
						.build())
				.build();
    	EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
    			.emissionsMonitoringPlan(emp)
    			.empVariationDetails(EmpVariationUkEtsDetails.builder()
    					.changes(List.of(EmpVariationUkEtsChangeType.DIFFERENT_FUMMS))
    					.reason("reason1")
    					.build())
    			.reasonRegulatorLed(EmpVariationUkEtsRegulatorLedReason.builder()
    					.type(EmpVariationUkEtsRegulatorLedReasonType.FAILED_TO_COMPLY_OR_APPLY)
    					.build())
    			.decisionNotification(DecisionNotification.builder()
    					.operators(Set.of("op1"))
    					.build())
    			.reviewGroupDecisionsRegulatorLed(Map.of(
    					EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder().build()
    					))
    			.originalEmpContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
    					.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
    							.abbreviations(EmpAbbreviations.builder().exist(false).build())
    							.build())
    					.build())
    			.empSectionsCompleted(Map.of("section1", List.of(Boolean.TRUE)))
    			.empAttachments(Map.of(att1, "att1.pdf"))
    			.build();
    	
    	ServiceContactDetails scd = ServiceContactDetails.builder()
    			.email("email")
    			.name("name")
    			.roleCode("roleCode")
    			.build();
    	
    	RequestAviationAccountInfo requestAviationAccountInfo = RequestAviationAccountInfo.builder()
    			.serviceContactDetails(scd)
    			.crcoCode("crcoCode2")
    			.build();
    	
    	Map<String, RequestActionUserInfo> usersInfo = new HashMap<>(Map.of(
    			"userId1", RequestActionUserInfo.builder().name("user1").build()
    			));
    	
    	EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload result = mapper.toEmpVariationApplicationRegulatorLedApprovedRequestActionPayload(requestPayload, requestAviationAccountInfo, usersInfo);
    	
    	assertThat(result).isEqualTo(EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload.builder()
    			.payloadType(RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD)
    			.decisionNotification(DecisionNotification.builder()
    					.operators(Set.of("op1"))
    					.build())
    			.usersInfo(usersInfo)
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
    					.abbreviations(EmpAbbreviations.builder().exist(true).build())
    					.operatorDetails(EmpOperatorDetails.builder()
    							.operatorName("opName1")
    							.crcoCode("crcoCode2")
    							.build())
    					.build())
    			.empVariationDetails(EmpVariationUkEtsDetails.builder()
    					.changes(List.of(EmpVariationUkEtsChangeType.DIFFERENT_FUMMS))
    					.reason("reason1")
    					.build())
    			.reasonRegulatorLed(EmpVariationUkEtsRegulatorLedReason.builder()
    					.type(EmpVariationUkEtsRegulatorLedReasonType.FAILED_TO_COMPLY_OR_APPLY)
    					.build())
    			.reviewGroupDecisions(Map.of(
    					EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder().build()
    					))
    			.originalEmpContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
    					.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
    							.abbreviations(EmpAbbreviations.builder().exist(false).build())
    							.build())
    					.build())
    			.serviceContactDetails(requestAviationAccountInfo.getServiceContactDetails())
    			.empSectionsCompleted(Map.of("section1", List.of(Boolean.TRUE)))
    			.empAttachments(Map.of(att1, "att1.pdf"))
    			.build());
    }
    
    @Test
    void cloneRegulatorLedApprovedPayloadIgnoreDecisionNotes() {
    	EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload source = EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload
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
				.reasonRegulatorLed(EmpVariationUkEtsRegulatorLedReason.builder().type(EmpVariationUkEtsRegulatorLedReasonType.OTHER).reasonOtherSummary("other").build())
				.build();
    	
    	EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload result = mapper.cloneRegulatorLedApprovedPayloadIgnoreDecisionNotes(source);
    	
    	assertThat(result.getEmissionsMonitoringPlan()).isEqualTo(source.getEmissionsMonitoringPlan());
    	assertThat(result.getEmpVariationDetails()).isEqualTo(source.getEmpVariationDetails());
    	assertThat(result.getDecisionNotification()).isEqualTo(source.getDecisionNotification());
    	assertThat(result.getReasonRegulatorLed()).isEqualTo(source.getReasonRegulatorLed());
    	assertThat(result.getReviewGroupDecisions()).containsExactlyEntriesOf(Map.of(
				EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
				.variationScheduleItems(List.of("item1", "item2"))
				.build()
				));
    }
    
    @Test
    void toEmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload() {
    	ServiceContactDetails scd = ServiceContactDetails.builder()
    			.email("email")
    			.name("name")
    			.roleCode("roleCode")
    			.build();
    	
    	RequestAviationAccountInfo requestAviationAccountInfo = RequestAviationAccountInfo.builder()
    			.serviceContactDetails(scd)
    			.crcoCode("crcoCode2")
    			.build();
    	
    	EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
				.abbreviations(EmpAbbreviations.builder().exist(true).build())
				.operatorDetails(EmpOperatorDetails.builder()
						.operatorName("opName1")
						.crcoCode("crcoCode1")
						.build())
				.build();
    	
    	
    	EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
    			.payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
    			.emissionsMonitoringPlan(emp)
    			.reviewGroupDecisionsRegulatorLed(Map.of(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
						.notes("notes")
						.variationScheduleItems(List.of("item1", "item2"))
						.build()))
    			.build();
    	
		EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload result = mapper
				.toEmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload(requestPayload,
						requestAviationAccountInfo,
						RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD);
		
		assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD);
		assertThat(result.getReasonRegulatorLed()).isEqualTo(requestPayload.getReasonRegulatorLed());
		assertThat(result.getReviewGroupDecisions()).isEqualTo(requestPayload.getReviewGroupDecisionsRegulatorLed());
		assertThat(result.getEmissionsMonitoringPlan()).isEqualTo(EmissionsMonitoringPlanUkEts.builder()
    					.abbreviations(EmpAbbreviations.builder().exist(true).build())
    					.operatorDetails(EmpOperatorDetails.builder()
    							.operatorName("opName1")
    							.crcoCode("crcoCode2")
    							.build())
    					.build());
    }
}
