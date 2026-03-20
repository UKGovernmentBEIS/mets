package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "#covidAdjustmentsOpinion != null or #covidAdjustmentsReviewNotes == null",
        message = "bdrs2.regulatorReview.covidAdjustments.notes.notAllowed"
)
@SpELExpression(
        expression = "#installationSectorOpinion != null or #installationSectorReviewNotes == null",
        message = "bdrs2.regulatorReview.installationSector.notes.notAllowed"
)
@SpELExpression(
        expression = "#cbamSplitOpinion != null or #cbamSplitReviewNotes == null",
        message = "bdrs2.regulatorReview.cbamSplit.notes.notAllowed"
)
public class BDRS2ApplicationRegulatorReviewOutcome {

    private BDRS2RegulatorReviewFreeAllocationOpinion freeAllocationOpinion;

    private BDRS2RegulatorReviewNotes freeAllocationReviewNotes;

    private BDRS2RegulatorReviewCovidAdjustmentsOpinion covidAdjustmentsOpinion;

    private BDRS2RegulatorReviewNotes covidAdjustmentsReviewNotes;

    private BDRS2RegulatorReviewInstallationSectorOpinion installationSectorOpinion;

    private BDRS2RegulatorReviewNotes installationSectorReviewNotes;

    private BDRS2RegulatorReviewCbamSplitOpinion cbamSplitOpinion;

    private BDRS2RegulatorReviewNotes cbamSplitReviewNotes;

    private UUID file;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();
}
