package uk.gov.pmrv.api.workflow.request.flow.common.vir.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{(#uncorrectedNonConformities?.size() gt 0) or (#priorYearIssues?.size() gt 0)  or (#recommendedImprovements?.size() gt 0)}", message = "virVerificationData.notEmpty")
public class VirVerificationData {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @JsonDeserialize(as = LinkedHashMap.class)
    private Map<String, @NotNull @Valid UncorrectedItem> uncorrectedNonConformities = new HashMap<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @JsonDeserialize(as = LinkedHashMap.class)
    private Map<String, @NotNull @Valid VerifierComment> priorYearIssues = new HashMap<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @JsonDeserialize(as = LinkedHashMap.class)
    private Map<String, @NotNull @Valid VerifierComment> recommendedImprovements = new HashMap<>();
}
