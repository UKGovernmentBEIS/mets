package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowUpActionType {

    MISSTATEMENT("Misstatement"),
    NON_CONFORMITY("Non conformity"),
    NON_COMPLIANCE("Non compliance"),
    RECOMMENDED_IMPROVEMENT("Recommended improvement"),
    UNRESOLVED_ISSUE_FROM_PREVIOUS_AUDIT("Unresolved issue from previous audit");

    private final String description;
}
