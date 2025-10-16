package uk.gov.pmrv.api.integration.registry.accountcreated.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedRegistryDTO {

    private AccountCreatedRegistryDetails accountCreatedRegistryDetails;
    private AccountCreatedRegistryHolderDetails accountCreatedRegistryHolderDetails;
}
