package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitTransferAApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @NotNull
    @Valid
    @JsonUnwrapped
    private PermitTransferDetails permitTransferDetails;

    @Builder.Default
    private Map<UUID, String> transferAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getTransferAttachments();
    }
}
