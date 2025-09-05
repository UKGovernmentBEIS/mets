package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.DoalDetermination;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#conservativeDeterminesActivity and #conservativeDeterminesActivityComment != null and #conservativeDeterminesActivityComment.trim().length() > 0) or " +
                "(#conservativeDeterminesActivity == false and " +
                "(#conservativeDeterminesActivityComment == null or #conservativeDeterminesActivityComment?.trim()?.length() == 0))}",
        message = "alr.regulator.review.outcome.conservativeDeterminesActivity"
)
public class ALRApplicationRegulatorReviewOutcome {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@Valid @NotNull ALRActivityLevel> activityLevels = new ArrayList<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@Valid @NotNull HistoricalActivityLevel> historicalActivityLevels = new ArrayList<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<@Valid @NotNull ALRPreliminaryAllocation> allocations = new HashSet<>();

    @NotNull
    private Boolean conservativeDeterminesActivity;

    private String conservativeDeterminesActivityComment;

    private String ukEtsAuthorityComments;

    //reusing DoalDetermination
    @Valid
    @NotNull
    private DoalDetermination determination;
}
