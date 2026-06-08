package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums;

import java.util.Set;

public enum NERReviewGroup {

    NER,
    OPINION_STATEMENT,
    OVERALL_DECISION;

    public static Set<NERReviewGroup> getVerificationDataReviewGroups() {
        return Set.of(
                NERReviewGroup.OPINION_STATEMENT,
                NERReviewGroup.OVERALL_DECISION
        );
    }

    public static Set<NERReviewGroup> getBDRS2DataReviewGroups() {
        return Set.of(
                NERReviewGroup.NER
        );
    }
}
