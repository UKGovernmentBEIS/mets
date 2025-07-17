package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import java.util.Set;

public enum BDRReviewGroup {
    BDR,
    OPINION_STATEMENT,
    OVERALL_DECISION;

    public static Set<BDRReviewGroup> getVerificationDataReviewGroups() {

        return Set.of(
            BDRReviewGroup.OPINION_STATEMENT,
            BDRReviewGroup.OVERALL_DECISION
        );
    }

    public static Set<BDRReviewGroup> getBDRDataReviewGroups() {
        return Set.of(
            BDRReviewGroup.BDR
        );
    }

}
