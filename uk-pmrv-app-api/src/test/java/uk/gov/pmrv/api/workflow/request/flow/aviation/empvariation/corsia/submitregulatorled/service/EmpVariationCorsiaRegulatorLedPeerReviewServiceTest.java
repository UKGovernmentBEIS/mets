package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaChangeType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRegulatorLedPeerReviewServiceTest {

	@InjectMocks
    private EmpVariationCorsiaRegulatorLedPeerReviewService cut;
	
	@Test
	void saveRequestPeerReviewAction() {
		UUID att1 = UUID.randomUUID();
		EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder()
				.abbreviations(EmpAbbreviations.builder().exist(true).build())
				.operatorDetails(EmpCorsiaOperatorDetails.builder()
						.operatorName("opName1")
						.build())
				.build();
		EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload
				.builder()
				.reasonRegulatorLed("reasonRegulatorLed")
				.emissionsMonitoringPlan(emp)
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("detailsReason")
						.changes(List.of(EmpVariationCorsiaChangeType.NEW_FUEL_TYPE))
						.build())
				.empAttachments(Map.of(att1, "att1.pdf"))
				.empSectionsCompleted(Map.of("section1", List.of(Boolean.TRUE)))
				.empVariationDetailsCompleted(Boolean.TRUE)
				.reviewGroupDecisions(Map.of(
					EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder().build()
    					))
				.build();
		
		String selectedPeerReviewer = "peerReviewr";
		String appUserId = "userId";
		
		EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
				.build();
		Request request = Request.builder().payload(requestPayload).build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(requestTaskPayload)
				.request(request)
				.build();
		
		cut.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, appUserId);
		
		assertThat(requestPayload.getRegulatorPeerReviewer()).isEqualTo(selectedPeerReviewer);
		assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(appUserId);
		assertThat(requestPayload.getEmissionsMonitoringPlan()).isEqualTo(emp);
		assertThat(requestPayload.getEmpVariationDetails()).isEqualTo(requestTaskPayload.getEmpVariationDetails());
		assertThat(requestPayload.getEmpAttachments()).isEqualTo(requestTaskPayload.getEmpAttachments());
		assertThat(requestPayload.getEmpSectionsCompleted()).isEqualTo(requestTaskPayload.getEmpSectionsCompleted());
		assertThat(requestPayload.getEmpVariationDetailsCompleted()).isEqualTo(requestTaskPayload.getEmpVariationDetailsCompleted());
		assertThat(requestPayload.getReviewGroupDecisionsRegulatorLed()).isEqualTo(requestTaskPayload.getReviewGroupDecisions());
		assertThat(requestPayload.getReasonRegulatorLed()).isEqualTo(requestTaskPayload.getReasonRegulatorLed());
	}
}
