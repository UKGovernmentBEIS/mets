package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.validator;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsRequestPeerReviewRegulatorLedValidator {

	private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
	private final EmpVariationRegulatorLedValidator empValidator;

	public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload payload,
			final AppUser appUser) {
		peerReviewerTaskAssignmentValidator.validate(
				RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW, payload.getPeerReviewer(),
				appUser);
		empValidator.validateEmp((EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload) requestTask
				.getPayload());
	}
}
