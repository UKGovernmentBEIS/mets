package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

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
public class AviationAccountCreatedRegistryDTO extends AccountCreatedRegistryDTO {

    private AviationAccountCreatedRegistryDetails accountCreatedRegistryDetails;
    private AviationAccountCreatedRegistryHolderDetails aviationAccountCreatedRegistryHolderDetails;

}
