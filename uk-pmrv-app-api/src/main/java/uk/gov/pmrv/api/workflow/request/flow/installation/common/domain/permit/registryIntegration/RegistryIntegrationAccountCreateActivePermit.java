package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryIntegrationAccountCreateActivePermit extends RegistryIntegrationActivePermit{

    private String emitterId;
    private String regulator;


}
