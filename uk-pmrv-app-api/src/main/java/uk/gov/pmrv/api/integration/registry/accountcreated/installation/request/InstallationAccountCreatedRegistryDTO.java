package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRegistryDTO;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class InstallationAccountCreatedRegistryDTO extends AccountCreatedRegistryDTO {

    private InstallationAccountCreatedRegistryDetails accountCreatedRegistryDetails;
    private InstallationAccountCreatedRegistryHolderDetails accountCreatedRegistryHolderDetails;

}
