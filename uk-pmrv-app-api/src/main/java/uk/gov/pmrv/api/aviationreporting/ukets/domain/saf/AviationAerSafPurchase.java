package uk.gov.pmrv.api.aviationreporting.ukets.domain.saf;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#sustainabilityCriteriaEvidenceType eq 'OTHER') == (#otherSustainabilityCriteriaEvidenceDescription != null)}",
        message = "aviationAer.saf.purchases.description")
public class AviationAerSafPurchase {

    @NotBlank
    @Size(max = 500)
    private String fuelName;

    @NotBlank
    @Size(max = 500)
    private String batchNumber;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal safMass;

    @NotNull
    private SustainabilityCriteriaEvidenceType sustainabilityCriteriaEvidenceType;

    @Size(max = 100)
    private String otherSustainabilityCriteriaEvidenceDescription;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> evidenceFiles = new HashSet<>();
}
