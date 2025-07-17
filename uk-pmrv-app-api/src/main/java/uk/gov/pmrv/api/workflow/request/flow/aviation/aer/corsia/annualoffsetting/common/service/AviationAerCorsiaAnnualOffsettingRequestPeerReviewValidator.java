package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final AviationAerCorsiaAnnualOffsettingValidatorService aviationAerCorsiaAnnualOffsettingValidatorService;

    public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload taskActionPayload,
                         final AppUser appUser) {
        final AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload taskPayload = (AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload) requestTask
                .getPayload();

        peerReviewerTaskAssignmentValidator.validate(requestTask.getType(), taskActionPayload.getPeerReviewer(), appUser);

        aviationAerCorsiaAnnualOffsettingValidatorService.validateAviationAerCorsiaAnnualOffsetting(taskPayload.getAviationAerCorsiaAnnualOffsetting());
    }
}
