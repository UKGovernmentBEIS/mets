package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class PermitSurrenderReviewDeterminationHandlerService {

private final List<PermitSurrenderReviewDeterminationHandler> handlers;

    public void handleDeterminationUponDecision(PermitSurrenderReviewDeterminationType determinationType, PermitSurrenderApplicationReviewRequestTaskPayload taskPayload,
            PermitSurrenderReviewDecision reviewDecision) {
        resolveHandler(determinationType)
            .handleDeterminationUponDecision(taskPayload, reviewDecision);
    }
    
    public void validateDecisionUponDetermination(PermitSurrenderApplicationReviewRequestTaskPayload taskPayload,
            PermitSurrenderReviewDetermination reviewDetermination) {
        resolveHandler(reviewDetermination.getType())
            .validateDecisionUponDetermination(taskPayload);
    }
    
    public void validateReview(PermitSurrenderReviewDecision reviewDecision,
                               @NotNull @Valid PermitSurrenderReviewDetermination reviewDetermination) {
        resolveHandler(reviewDetermination.getType())
            .validateReview(reviewDecision, reviewDetermination);
    }
    
    private PermitSurrenderReviewDeterminationHandler resolveHandler(PermitSurrenderReviewDeterminationType determinationType) {
        return  handlers.stream()
            .filter(v -> v.getType().equals(determinationType))
            .findFirst()
            .orElseThrow(() -> {
                throw new RuntimeException("No handler found for type: " + determinationType);
            });
    }
}
