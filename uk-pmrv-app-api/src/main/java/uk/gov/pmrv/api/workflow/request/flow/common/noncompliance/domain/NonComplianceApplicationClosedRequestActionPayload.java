package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceApplicationClosedRequestActionPayload extends RequestActionPayload {

    @NotNull
    @Valid
    @JsonUnwrapped
    private NonComplianceCloseJustification closeJustification;

    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNonComplianceAttachments();
    }
}
