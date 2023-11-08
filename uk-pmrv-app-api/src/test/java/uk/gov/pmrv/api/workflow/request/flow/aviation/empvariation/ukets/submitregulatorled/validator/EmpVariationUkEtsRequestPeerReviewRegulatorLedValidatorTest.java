package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.validator;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsRequestPeerReviewRegulatorLedValidatorTest {

	@InjectMocks
    private EmpVariationUkEtsRequestPeerReviewRegulatorLedValidator cut;
	
	@Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
	
	@Mock
    private EmpVariationRegulatorLedValidator empValidator;
	
	@Test
	void validate() {
		EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload
				.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
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
		
		verify(peerReviewerTaskAssignmentValidator, timeout(1)).validate(RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW, payload.getPeerReviewer(),
				pmrvUser);
		
		verify(empValidator, times(1)).validateEmp(requestTaskPayload);
	}
}
