package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BDR {

    @NotNull
    private Boolean isApplicationForFreeAllocation;

    @NotNull
    private BDRStatusApplicationType statusApplicationType;

    @NotNull
    private Boolean infoIsCorrectChecked;

    @NotNull
    private Boolean hasMmp;

    @NotNull
    private UUID bdrFile;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> mmpFiles = new HashSet<>();
}
