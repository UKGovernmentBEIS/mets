package uk.gov.pmrv.api.reporting.domain.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#areThereUncorrectedNonConformities) == (#uncorrectedNonConformities?.size() gt 0)}", message = "aerVerificationData.uncorrectedNonConformities.uncorrectedNonConformities")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#areTherePriorYearIssues) == (#priorYearIssues?.size() gt 0)}", message = "aerVerificationData.uncorrectedNonConformities.priorYearIssues")
public class UncorrectedNonConformities {

    @NotNull
    private Boolean areThereUncorrectedNonConformities;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotNull UncorrectedItem> uncorrectedNonConformities = new HashSet();

    @NotNull
    private Boolean areTherePriorYearIssues;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotNull VerifierComment> priorYearIssues = new HashSet<>();
}
