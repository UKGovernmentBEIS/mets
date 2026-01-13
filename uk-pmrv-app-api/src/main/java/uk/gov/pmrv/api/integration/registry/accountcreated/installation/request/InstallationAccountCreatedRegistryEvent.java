package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationAccountCreatedRegistryEvent {

    private Long accountId;
    private String requestId;

}
