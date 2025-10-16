package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryIntegrationEventError {

    private RegistryResponseErrorCode error;
    private String errorMessage;

}
