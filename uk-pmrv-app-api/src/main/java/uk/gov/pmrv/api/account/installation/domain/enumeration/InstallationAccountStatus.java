package uk.gov.pmrv.api.account.installation.domain.enumeration;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;

public enum InstallationAccountStatus implements AccountStatus {
    UNAPPROVED,
    DENIED,
    NEW,
    LIVE,
    DEEMED_WITHDRAWN,
    PERMIT_REFUSED,
    AWAITING_SURRENDER,
    SURRENDERED,
    AWAITING_REVOCATION,
    REVOKED,
    AWAITING_TRANSFER,
    TRANSFERRED
    ;

    @Override
    public String getName() {
        return this.name();
    }
}
