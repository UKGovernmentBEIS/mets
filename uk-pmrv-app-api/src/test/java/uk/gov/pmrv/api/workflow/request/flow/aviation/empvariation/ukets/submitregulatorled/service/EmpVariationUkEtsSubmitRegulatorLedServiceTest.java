package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsSubmitRegulatorLedServiceTest {

	@InjectMocks
    private EmpVariationUkEtsSubmitRegulatorLedService cut;
	
	@Test
	void saveEmpVariation() {
		EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload taskActionPayload = EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.empSectionsCompleted(Map.of("ABBREV", List.of(Boolean.FALSE)))
				.empVariationDetails(EmpVariationUkEtsDetails.builder()
						.reason("reason")
						.build())
				.empVariationDetailsCompleted(Boolean.TRUE)
				.reasonRegulatorLed(EmpVariationUkEtsRegulatorLedReason.builder()
						.type(EmpVariationUkEtsRegulatorLedReasonType.FAILED_TO_COMPLY_OR_APPLY)
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
						.build())
				.build();
		
		cut.saveEmpVariation(taskActionPayload, requestTask);
		
		assertThat(requestTask.getPayload()).isEqualTo(EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.emissionsMonitoringPlan(taskActionPayload.getEmissionsMonitoringPlan())
				.empSectionsCompleted(taskActionPayload.getEmpSectionsCompleted())
				.empVariationDetails(taskActionPayload.getEmpVariationDetails())
				.empVariationDetailsCompleted(taskActionPayload.getEmpVariationDetailsCompleted())
				.reasonRegulatorLed(taskActionPayload.getReasonRegulatorLed())
				.build());
	}
	
	@Test
	void saveReviewGroupDecision() {
		EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload requestTaskActionPayload = EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload.builder()
				.decision(EmpAcceptedVariationDecisionDetails.builder()
						.notes("notes2")
						.variationScheduleItems(List.of("var1", "var2"))
						.build())
				.group(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
				.empSectionsCompleted(Map.of("ABBR", List.of(Boolean.TRUE)))
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
						.reviewGroupDecisions(new HashMap<>(Map.of(
								EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
									.notes("notes1")
									.variationScheduleItems(List.of("var1"))
									.build(),
									
								EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpAcceptedVariationDecisionDetails.builder()
									.notes("notes")
									.build()
								)))
						.build())
				.build();
		
		cut.saveReviewGroupDecision(requestTaskActionPayload, requestTask);
		
		assertThat(requestTask.getPayload()).isEqualTo(
				EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.empSectionsCompleted(Map.of("ABBR", List.of(Boolean.TRUE)))
				.reviewGroupDecisions(Map.of(
						EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
							.notes("notes2")
							.variationScheduleItems(List.of("var1", "var2"))
							.build(),
						EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpAcceptedVariationDecisionDetails.builder()
							.notes("notes")
							.build()
						))
				.build());
	}
	
	@Test
	void saveDecisionNotification() {
		EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
				.build();
		Request request = Request.builder().payload(requestPayload).build();
		
		UUID att1 = UUID.randomUUID();
		EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.empVariationDetails(EmpVariationUkEtsDetails.builder()
						.reason("reason")
						.build())
				.empAttachments(Map.of(att1, "att1.pdf"))
				.empSectionsCompleted(Map.of("ABBREV", List.of(Boolean.FALSE)))
				.empVariationDetailsCompleted(Boolean.TRUE)
				.reviewGroupDecisions(Map.of(
						EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
						EmpAcceptedVariationDecisionDetails.builder().notes("notes").build()
						))
				.reasonRegulatorLed(EmpVariationUkEtsRegulatorLedReason.builder()
						.type(EmpVariationUkEtsRegulatorLedReasonType.FAILED_TO_COMPLY_OR_APPLY)
						.build())
				.build();
		RequestTask requestTask = RequestTask.builder()
				.payload(taskPayload)
				.request(request)
				.build();
		DecisionNotification decisionNotification = DecisionNotification.builder()
				.operators(Set.of("op1"))
				.build();
		PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
		
		cut.saveDecisionNotification(requestTask, decisionNotification, pmrvUser);
		
		assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(pmrvUser.getUserId());
		assertThat(requestPayload.getEmissionsMonitoringPlan()).isEqualTo(taskPayload.getEmissionsMonitoringPlan());
		assertThat(requestPayload.getEmpVariationDetails()).isEqualTo(taskPayload.getEmpVariationDetails());
		assertThat(requestPayload.getEmpAttachments()).isEqualTo(taskPayload.getAttachments());
		assertThat(requestPayload.getEmpSectionsCompleted()).isEqualTo(taskPayload.getEmpSectionsCompleted());
		assertThat(requestPayload.getEmpVariationDetailsCompleted()).isEqualTo(taskPayload.getEmpVariationDetailsCompleted());
		assertThat(requestPayload.getReviewGroupDecisionsRegulatorLed()).isEqualTo(taskPayload.getReviewGroupDecisions());
		assertThat(requestPayload.getReasonRegulatorLed()).isEqualTo(taskPayload.getReasonRegulatorLed());
		assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
	}
}
