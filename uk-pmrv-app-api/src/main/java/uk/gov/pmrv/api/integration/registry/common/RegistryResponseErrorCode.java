package uk.gov.pmrv.api.integration.registry.common;

import lombok.Getter;

@Getter
public enum RegistryResponseErrorCode {

    ERROR_0803("ERROR_0803", "Operator ID does not exist in Registry."),
    ERROR_0805("ERROR_0805", "The Operator ID is associated with an Account with status Closed."),
    ERROR_0806("ERROR_0806", "The Operator ID is associated with an Account with status Transfer Pending."),
    ERROR_0807("ERROR_0807", "The Operator ID is associated with an Account with status Closure Pending."),
    ERROR_0808("ERROR_0808", "The Account is marked as EXCLUDED for the year provided."),
    ERROR_0812("ERROR_0812", "The Year must not be equal to the current year, except if current year is the same as the Last Year of Verified Emissions (LYVE)."),
    ERROR_0813("ERROR_0813", "The Year must not be before the First Year of Verified Emissions (FYVE)."),
    ERROR_0814("ERROR_0814", "The Year must not be after the Last Year of Verified Emissions (LYVE).");

    private final String code;
    private final String description;

    RegistryResponseErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
