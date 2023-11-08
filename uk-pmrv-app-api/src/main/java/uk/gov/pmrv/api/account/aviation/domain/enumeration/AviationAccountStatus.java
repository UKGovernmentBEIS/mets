package uk.gov.pmrv.api.account.aviation.domain.enumeration;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;

public enum AviationAccountStatus implements AccountStatus {
    NEW,
    LIVE,
    CLOSED
    ;

    @Override
    public String getName() {
        return this.name();
    }
}
