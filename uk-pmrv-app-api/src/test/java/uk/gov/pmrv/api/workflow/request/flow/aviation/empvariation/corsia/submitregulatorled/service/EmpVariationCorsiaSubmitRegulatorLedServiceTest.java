package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service;

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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaSubmitRegulatorLedServiceTest {

	@InjectMocks
    private EmpVariationCorsiaSubmitRegulatorLedService cut;
	
	@Test
	void saveEmpVariation() {
		
		EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload taskActionPayload = EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.empSectionsCompleted(Map.of("ABBREV", List.of(Boolean.FALSE)))
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("reason")
						.build())
				.empVariationDetailsCompleted(Boolean.TRUE)
				.reasonRegulatorLed("reasonRegulatorLed")
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
						.build())
				.build();
		
		cut.saveEmpVariation(taskActionPayload, requestTask);
		
		assertThat(requestTask.getPayload()).isEqualTo(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.emissionsMonitoringPlan(taskActionPayload.getEmissionsMonitoringPlan())
				.empSectionsCompleted(taskActionPayload.getEmpSectionsCompleted())
				.empVariationDetails(taskActionPayload.getEmpVariationDetails())
				.empVariationDetailsCompleted(taskActionPayload.getEmpVariationDetailsCompleted())
				.reasonRegulatorLed(taskActionPayload.getReasonRegulatorLed())
				.build());
	}
	
	@Test
	void saveReviewGroupDecision() {
		
		EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload requestTaskActionPayload =
			EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload.builder()
				.decision(EmpAcceptedVariationDecisionDetails.builder()
						.notes("notes2")
						.variationScheduleItems(List.of("var1", "var2"))
						.build())
				.group(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
				.empSectionsCompleted(Map.of("ABBR", List.of(Boolean.TRUE)))
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
						.reviewGroupDecisions(new HashMap<>(Map.of(
							EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
									.notes("notes1")
									.variationScheduleItems(List.of("var1"))
									.build(),

							EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpAcceptedVariationDecisionDetails.builder()
									.notes("notes")
									.build()
								)))
						.build())
				.build();
		
		cut.saveReviewGroupDecision(requestTaskActionPayload, requestTask);
		
		assertThat(requestTask.getPayload()).isEqualTo(
				EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.empSectionsCompleted(Map.of("ABBR", List.of(Boolean.TRUE)))
				.reviewGroupDecisions(Map.of(
					EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
							.notes("notes2")
							.variationScheduleItems(List.of("var1", "var2"))
							.build(),
					EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpAcceptedVariationDecisionDetails.builder()
							.notes("notes")
							.build()
						))
				.build());
	}

	@Test
	void saveDecisionNotification() {
		
		EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
			.build();
		Request request = Request.builder().payload(requestPayload).build();

		UUID att1 = UUID.randomUUID();
		EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
			.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
				.abbreviations(EmpAbbreviations.builder()
					.exist(true)
					.build())
				.build())
			.empVariationDetails(EmpVariationCorsiaDetails.builder()
				.reason("reason")
				.build())
			.empAttachments(Map.of(att1, "att1.pdf"))
			.empSectionsCompleted(Map.of("ABBREV", List.of(Boolean.FALSE)))
			.empVariationDetailsCompleted(Boolean.TRUE)
			.reviewGroupDecisions(Map.of(
				EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
				EmpAcceptedVariationDecisionDetails.builder().notes("notes").build()
			))
			.reasonRegulatorLed("reasonRegulatorLed")
			.build();
		RequestTask requestTask = RequestTask.builder()
			.payload(taskPayload)
			.request(request)
			.build();
		DecisionNotification decisionNotification = DecisionNotification.builder()
			.operators(Set.of("op1"))
			.build();
		AppUser appUser = AppUser.builder().userId("userId").build();

		cut.saveDecisionNotification(requestTask, decisionNotification, appUser);

		assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(appUser.getUserId());
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
