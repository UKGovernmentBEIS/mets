package uk.gov.pmrv.api.integration.registry.accountcreated.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountCreatedRegistryHolderDetails {

    private String accountHolderType;
    private String organisationName;
    private String individualName;
    private String companyRegistrationNumber;
    private String addressLine1;
    private String addressLine2;
    private String townOrCity;
    private String stateOrProvince;
    private String postalCode;
    private String country;



}
