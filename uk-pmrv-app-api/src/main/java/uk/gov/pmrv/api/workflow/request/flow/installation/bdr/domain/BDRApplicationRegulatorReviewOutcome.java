package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
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
public class BDRApplicationRegulatorReviewOutcome {

    private Boolean hasRegulatorSentFreeAllocation;

    private String freeAllocationNotes;

    private String freeAllocationNotesOperator;

    private Boolean hasRegulatorSentHSE;

    private Boolean hasRegulatorSentUSE;

    private String useHseNotes;

    private String useHseNotesOperator;

    private Boolean hasOperatorMetDataSubmissionRequirements;

    private UUID bdrFile;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}
