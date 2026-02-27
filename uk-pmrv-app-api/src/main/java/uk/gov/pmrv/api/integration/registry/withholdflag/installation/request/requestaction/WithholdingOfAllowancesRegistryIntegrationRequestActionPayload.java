package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request.requestaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.time.Year;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class WithholdingOfAllowancesRegistryIntegrationRequestActionPayload extends RequestActionPayload {

    private Integer registryId;
    private Boolean withholdFlag;
    private Year withholdYear;

}
