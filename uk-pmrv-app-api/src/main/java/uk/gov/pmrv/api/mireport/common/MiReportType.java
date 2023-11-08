package uk.gov.pmrv.api.mireport.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Competent authorities.
 */
@Getter
@AllArgsConstructor
public enum MiReportType {

    LIST_OF_ACCOUNTS_USERS_CONTACTS("List of Accounts, Users and Contacts"),
    COMPLETED_WORK("Completed work"),
    REGULATOR_OUTSTANDING_REQUEST_TASKS("Regulator outstanding request tasks"),
    LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS("List of Accounts and Assigned Regulator Site Contacts"),
    LIST_OF_VERIFICATION_BODY_USERS("List of Verification bodies and Users"),
    CUSTOM("Custom");

    private final String description;

    public String getName() {
        return this.name();
    }
}
