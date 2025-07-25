package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class DreRequestPeerReviewValidator {

	private final DreValidatorService dreValidatorService;
	private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

	public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload taskActionPayload,
			final AppUser appUser) {
		final DreApplicationSubmitRequestTaskPayload taskPayload = (DreApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		
		peerReviewerTaskAssignmentValidator.validate(RequestTaskType.DRE_APPLICATION_PEER_REVIEW,
				taskActionPayload.getPeerReviewer(), appUser);
		
		dreValidatorService.validateDre(taskPayload.getDre());
	}

}
