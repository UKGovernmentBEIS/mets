package uk.gov.pmrv.api.reporting.domain.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#areThereUncorrectedMisstatements) == (#uncorrectedMisstatements?.size() gt 0)}", message = "aerVerificationData.uncorrectedMisstatements.uncorrectedMisstatements")
public class UncorrectedMisstatements {

    @NotNull
    private Boolean areThereUncorrectedMisstatements;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotNull UncorrectedItem> uncorrectedMisstatements = new HashSet<>();
}
