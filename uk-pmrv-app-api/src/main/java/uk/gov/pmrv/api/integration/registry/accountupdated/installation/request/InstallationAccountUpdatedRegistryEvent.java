package uk.gov.pmrv.api.integration.registry.accountupdated.installation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationAccountUpdatedRegistryEvent {

    private Long accountId;
    private String requestId;
    private boolean isFromSetOperatorId;

}