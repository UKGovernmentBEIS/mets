package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRegistryDetails;

import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class InstallationAccountCreatedRegistryDetails extends AccountCreatedRegistryDetails {

    private String permitId;
    private String installationName;
    private List<String> installationActivityTypes;



}
