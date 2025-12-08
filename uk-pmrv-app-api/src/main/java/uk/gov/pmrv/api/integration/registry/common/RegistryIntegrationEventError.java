package uk.gov.pmrv.api.integration.registry.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryIntegrationEventError {

    private RegistryResponseErrorCode error;
    private String errorMessage;

}
