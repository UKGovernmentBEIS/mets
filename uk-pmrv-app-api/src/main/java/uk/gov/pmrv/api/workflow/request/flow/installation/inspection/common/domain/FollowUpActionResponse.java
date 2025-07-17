package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FollowUpActionResponse {

    @NotNull
    private Boolean completed;

    @NotNull
    private String explanation;

    @Builder.Default
    private Set<UUID> followUpActionResponseAttachments = new HashSet<>();

    private LocalDate completionDate;
}
