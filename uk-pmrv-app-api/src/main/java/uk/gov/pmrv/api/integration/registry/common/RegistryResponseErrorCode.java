package uk.gov.pmrv.api.integration.registry.common;

import lombok.Getter;

@Getter
public enum RegistryResponseErrorCode {

    ERROR_0101("ERROR_0101", "Data outside expected format"),
    ERROR_0102("ERROR_0102", "Emitter ID already exists in Registry"),
    ERROR_0103("ERROR_0103", "Mandatory field is not provided"),
    ERROR_0104("ERROR_0104", "CRN or Justification not provided"),
    ERROR_0105("ERROR_0105", "Permit/EMP ID already exists in Registry"),
    ERROR_0107("ERROR_0107", "Country Code does not exist"),
    ERROR_0108("ERROR_0108", "Regulator value does not exist"),
    ERROR_0109("ERROR_0109", "Account Holder Type does not exist"),
    ERROR_0111("ERROR_0111", "First Year Verified Emissions (FYVE) value is before 2021"),
    ERROR_0112("ERROR_0112", "Activity type does not exist"),
    ERROR_0113("ERROR_0113", "Invalid account type"),
    ERROR_0200("ERROR_0200", "Internal Server Error"),
    ERROR_0201("ERROR_0201", "Data validation error"),
    ERROR_0202("ERROR_0202", "Emitter ID does not exist in METS"),
    ERROR_0203("ERROR_0203", "Emitter ID is already associated with an Operator ID"),
    ERROR_0204("ERROR_0204", "Operator ID already associated with another live account"),
    ERROR_0205("ERROR_0205", "Emitter ID is associated with an invalid type of account"),
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
