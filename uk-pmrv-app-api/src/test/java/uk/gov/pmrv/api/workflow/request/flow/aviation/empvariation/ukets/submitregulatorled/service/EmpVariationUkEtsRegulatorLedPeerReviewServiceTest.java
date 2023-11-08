package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsChangeType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReasonType;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsRegulatorLedPeerReviewServiceTest {

	@InjectMocks
    private EmpVariationUkEtsRegulatorLedPeerReviewService cut;
	
	@Test
	void saveRequestPeerReviewAction() {
		UUID att1 = UUID.randomUUID();
		EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
				.abbreviations(EmpAbbreviations.builder().exist(true).build())
				.operatorDetails(EmpOperatorDetails.builder()
						.operatorName("opName1")
						.crcoCode("crcoCode1")
						.build())
				.build();
		EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload
				.builder()
				.reasonRegulatorLed(EmpVariationUkEtsRegulatorLedReason.builder()
						.type(EmpVariationUkEtsRegulatorLedReasonType.FOLLOWING_IMPROVING_REPORT)
						.build())
				.emissionsMonitoringPlan(emp)
				.empVariationDetails(EmpVariationUkEtsDetails.builder()
						.reason("detailsReason")
						.changes(List.of(EmpVariationUkEtsChangeType.DIFFERENT_FUMMS))
						.build())
				.empAttachments(Map.of(att1, "att1.pdf"))
				.empSectionsCompleted(Map.of("section1", List.of(Boolean.TRUE)))
				.empVariationDetailsCompleted(Boolean.TRUE)
				.reviewGroupDecisions(Map.of(
    					EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder().build()
    					))
				.build();
		
		String selectedPeerReviewer = "peerReviewr";
		String pmrvUserId = "userId";
		
		EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
				.build();
		Request request = Request.builder().payload(requestPayload).build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(requestTaskPayload)
				.request(request)
				.build();
		
		cut.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, pmrvUserId);
		
		assertThat(requestPayload.getRegulatorPeerReviewer()).isEqualTo(selectedPeerReviewer);
		assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(pmrvUserId);
		assertThat(requestPayload.getEmissionsMonitoringPlan()).isEqualTo(emp);
		assertThat(requestPayload.getEmpVariationDetails()).isEqualTo(requestTaskPayload.getEmpVariationDetails());
		assertThat(requestPayload.getEmpAttachments()).isEqualTo(requestTaskPayload.getEmpAttachments());
		assertThat(requestPayload.getEmpSectionsCompleted()).isEqualTo(requestTaskPayload.getEmpSectionsCompleted());
		assertThat(requestPayload.getEmpVariationDetailsCompleted()).isEqualTo(requestTaskPayload.getEmpVariationDetailsCompleted());
		assertThat(requestPayload.getReviewGroupDecisionsRegulatorLed()).isEqualTo(requestTaskPayload.getReviewGroupDecisions());
		assertThat(requestPayload.getReasonRegulatorLed()).isEqualTo(requestTaskPayload.getReasonRegulatorLed());
	}
}
