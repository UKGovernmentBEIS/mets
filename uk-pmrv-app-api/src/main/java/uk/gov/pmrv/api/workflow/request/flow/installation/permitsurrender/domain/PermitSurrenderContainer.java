package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitSurrenderContainer {

    @Valid
    @NotNull
    private PermitSurrender permitSurrender;

    @Builder.Default
    private Map<UUID, String> permitSurrenderAttachments = new HashMap<>();
}
