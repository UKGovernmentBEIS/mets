package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#existUncorrectedNonConformities) == (#uncorrectedNonConformities?.size() gt 0)}", message = "aviationAerVerificationData.uncorrectedNonConformities.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#existPriorYearIssues) == (#priorYearIssues?.size() gt 0)}", message = "aviationAerVerificationData.uncorrectedNonConformities.priorYearIssues.exist")
public class AviationAerUncorrectedNonConformities {

    @NotNull
    private Boolean existUncorrectedNonConformities;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotNull UncorrectedItem> uncorrectedNonConformities = new HashSet();

    @NotNull
    private Boolean existPriorYearIssues;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotNull VerifierComment> priorYearIssues = new HashSet<>();
}