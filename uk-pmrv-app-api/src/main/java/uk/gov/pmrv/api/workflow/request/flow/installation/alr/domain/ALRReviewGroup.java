package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;


import java.util.Set;

public enum ALRReviewGroup {
    ALR,
    OPINION_STATEMENT,
    OVERALL_DECISION;

    public static Set<ALRReviewGroup> getVerificationDataReviewGroups() {

        return Set.of(
                ALRReviewGroup.OPINION_STATEMENT,
                ALRReviewGroup.OVERALL_DECISION
        );
    }

    public static Set<ALRReviewGroup> getALRDataReviewGroups() {
        return Set.of(
                ALRReviewGroup.ALR
        );
    }
}
