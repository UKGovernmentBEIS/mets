package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRegistryHolderDetails;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class AviationAccountCreatedRegistryHolderDetails extends AccountCreatedRegistryHolderDetails {
}
