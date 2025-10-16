package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRegistryHolderDetails;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstallationAccountCreatedRegistryHolderDetails extends AccountCreatedRegistryHolderDetails {

    private Boolean crnNotExist;
    private String crnJustification;

}
