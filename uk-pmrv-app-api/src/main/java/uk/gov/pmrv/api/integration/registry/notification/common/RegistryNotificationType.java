package uk.gov.pmrv.api.integration.registry.notification.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegistryNotificationType {

    SURRENDER_NOTIFICATION("Installation surrender request approval"),
    TRANSFER_NOTIFICATION("Installation transfer"),
    SURRENDER_CESSATION_NOTIFICATION("Installation surrender cessation completed"),
    AVIATION_ACCOUNT_CLOSED("Aviation account closed"),
    RETURN_OF_ALLOWANCES_NOTIFICATION("Installation return of allowances"),
    WITHHOLDING_OF_ALLOWANCES_WITHDRAWN_NOTIFICATION("Installation remove withhold of allowances"),
    EMP_ISSUANCE_DEEMED_WITHDRAWN_NOTIFICATION("Aviation EMP Withdrawn");

    private final String name;

}
