package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetOperatorIdEventOutcome {

    SetOperatorIdResponseEvent event;
    List<RegistryIntegrationEventError> errors;
    RegistryResponseStatus outcome;


}
