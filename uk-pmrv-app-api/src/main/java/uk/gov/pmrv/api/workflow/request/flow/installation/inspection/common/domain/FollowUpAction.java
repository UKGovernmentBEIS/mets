package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.FollowUpActionType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FollowUpAction {

    @NotNull
    private FollowUpActionType followUpActionType;

    @NotBlank
    @Size(max = 10000)
    private String explanation;

    @Builder.Default
    private Set<UUID> followUpActionAttachments = new HashSet<>();
}
