package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.validator;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRequestPeerReviewRegulatorLedValidatorTest {

	@InjectMocks
    private EmpVariationCorsiaRequestPeerReviewRegulatorLedValidator cut;
	
	@Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
	
	@Mock
    private EmpVariationCorsiaRegulatorLedValidator empValidator;
	
	@Test
	void validate() {
		
		EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload
				.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
						.abbreviations(EmpAbbreviations.builder()
								.exist(false)
								.build())
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();
		
		PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
				.peerReviewer("peer")
				.build();
		
		PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		
		cut.validate(requestTask, payload, pmrvUser);
		
		verify(peerReviewerTaskAssignmentValidator, timeout(1)).validate(RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW, payload.getPeerReviewer(),
				pmrvUser);
		
		verify(empValidator, times(1)).validateEmp(requestTaskPayload);
	}
}
