package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstallationInspection {

    @Valid
    @NotNull
    private InstallationInspectionDetails details;

    private Boolean followUpActionsRequired;

    private String followUpActionsOmissionJustification;

    private Set<UUID> followUpActionsOmissionFiles;

    @Builder.Default
    private List<@Valid @NotNull FollowUpAction> followUpActions = new ArrayList<>();

    @Future
    private LocalDate responseDeadline;
}
