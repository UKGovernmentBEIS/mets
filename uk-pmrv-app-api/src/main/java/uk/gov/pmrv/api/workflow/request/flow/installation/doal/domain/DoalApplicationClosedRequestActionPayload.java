package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DoalApplicationClosedRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private Doal doal;

    @Builder.Default
    private Map<UUID, String> doalAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getDoalAttachments();
    }
}
