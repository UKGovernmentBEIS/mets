package uk.gov.pmrv.api.aviationreporting.ukets.domain.saf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#emissionsFactor?.compareTo(T(java.math.BigDecimal).valueOf(3.15)) == 0}", message = "aviationAer.saf.emissionsFactor")
public class AviationAerSafDetails {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<@NotNull @Valid AviationAerSafPurchase> purchases = new ArrayList<>();

    @NotNull
    private UUID noDoubleCountingDeclarationFile;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal totalSafMass;

    @NotNull
    @Digits(integer = 1, fraction = 2)
    @Positive
    private BigDecimal emissionsFactor;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal totalEmissionsReductionClaim;

    @JsonIgnore
    public Set<UUID> getAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();
        if (noDoubleCountingDeclarationFile != null) {
            attachments.add(noDoubleCountingDeclarationFile);
        }
        if (purchases != null && !ObjectUtils.isEmpty(purchases)) {
            attachments.addAll(purchases.stream()
                    .map(AviationAerSafPurchase::getEvidenceFiles)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet()));
        }
        return Collections.unmodifiableSet(attachments);
    }
}
