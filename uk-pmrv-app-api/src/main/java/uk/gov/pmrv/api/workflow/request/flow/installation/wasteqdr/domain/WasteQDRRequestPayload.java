package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class WasteQDRRequestPayload extends RequestPayload {

    @NotNull
    private WasteQDR qdr;
}
