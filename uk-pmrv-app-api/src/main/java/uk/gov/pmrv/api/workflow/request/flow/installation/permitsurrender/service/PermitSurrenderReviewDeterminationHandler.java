package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

interface PermitSurrenderReviewDeterminationHandler<T extends PermitSurrenderReviewDetermination> {

    void handleDeterminationUponDecision(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload,
            PermitSurrenderReviewDecision reviewDecision);

    void validateDecisionUponDetermination(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload);
    
    void validateReview(PermitSurrenderReviewDecision reviewDecision, T reviewDetermination);

    PermitSurrenderReviewDeterminationType getType();
}
