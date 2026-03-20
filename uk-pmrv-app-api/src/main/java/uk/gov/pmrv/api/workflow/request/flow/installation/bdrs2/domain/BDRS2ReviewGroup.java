package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import java.util.Set;

public enum BDRS2ReviewGroup {
    BDRS2,
    OPINION_STATEMENT,
    OVERALL_DECISION;

    public static Set<BDRS2ReviewGroup> getVerificationDataReviewGroups() {
        return Set.of(
            BDRS2ReviewGroup.OPINION_STATEMENT,
            BDRS2ReviewGroup.OVERALL_DECISION
        );
    }

    public static Set<BDRS2ReviewGroup> getBDRS2DataReviewGroups() {
        return Set.of(
            BDRS2ReviewGroup.BDRS2
        );
    }
}
