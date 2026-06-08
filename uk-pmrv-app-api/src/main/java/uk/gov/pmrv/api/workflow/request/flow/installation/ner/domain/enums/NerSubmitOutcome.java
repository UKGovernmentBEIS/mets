package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums;

public enum NerSubmitOutcome {

    SUBMITTED_TO_VERIFIER,
    VERIFICATION_SUBMITTED_TO_OPERATOR,
    SUBMITTED,
    CANCELLED,
    PEER_REVIEW_REQUIRED,
}
