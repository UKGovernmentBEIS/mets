package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WithholdingOfAllowancesReasonType {

    ASSESSING_A_RENUNCIATION_NOTICE("Assessing a renunciation notice"),
    ASSESSING_A_SPLIT_OR_A_MERGER("Assessing a split or a merger"),
    ASSESSING_TRANSFER_OF_AVIATION_FREE_ALLOCATION_UNDER_ARTICLE_34Q("Assessing transfer of aviation free allocation " +
        "under article 34Q"),
    DETERMINING_APPLICATION_IN_RESPECT_OF_THE_COVID_YEAR("Determining application in respect of the Covid year"),
    DETERMINING_A_SURRENDER_APPLICATION("Determining a surrender application"),
    INVESTIGATING_AN_ERROR_IN_AVIATION_ALLOCATION_TABLE("Investigating an error in aviation allocation table"),
    INVESTIGATING_AN_ERROR_IN_ALLOCATION_TABLE("Investigating an error in allocation table"),
    INVESTIGATING_IF_PERSON_HAS_PERMANENTLY_CEASED_AN_AVIATION_ACTIVITY("Investigating if person has permanently " +
        "ceased an aviation activity"),
    INVESTIGATING_WHETHER_AN_INSTALLATION_HAS_CEASED_OPERATION("Investigating whether an installation has ceased " +
        "operation"),
    MONITORING_METHODOLOGY_PLAN_HAS_NOT_BEEN_APPROVED("Monitoring methodology plan has not been approved"),
    SURRENDER_OR_REVOCATION_NOTICE_HAS_NOT_YET_COME_INTO_EFFECT("Surrender or revocation notice has not yet come into" +
        " effect"),
    OTHER("Other (please specify)");

    private final String type;
}
