package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;

public enum PermitNotificationReviewDecisionType {
    ACCEPTED,
    REJECTED,
    PERMANENT_CESSATION,
    TEMPORARY_CESSATION,
    CESSATION_TREATED_AS_PERMANENT,
    NOT_CESSATION;

    public DeterminationType toDeterminationType() {
        return switch (this) {
            case PERMANENT_CESSATION, TEMPORARY_CESSATION, CESSATION_TREATED_AS_PERMANENT, NOT_CESSATION ->
                    DeterminationType.COMPLETED;
            case REJECTED -> DeterminationType.REJECTED;
            case ACCEPTED -> DeterminationType.GRANTED;
            default -> throw new UnsupportedOperationException("No determination for " + this);
        };
    }
}
