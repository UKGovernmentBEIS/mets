package uk.gov.pmrv.api.integration.registry.accountcreated.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountCreatedRegistryDetails {

    private RegistryAccountType accountType;
    private String emitterId;
    private String regulator;
    private String accountName;

}
