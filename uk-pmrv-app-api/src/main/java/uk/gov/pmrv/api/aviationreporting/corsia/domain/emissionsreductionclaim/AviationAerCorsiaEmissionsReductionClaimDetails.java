package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerCorsiaEmissionsReductionClaimDetails {

    @Builder.Default
    @NotEmpty
    private Set<UUID> cefFiles = new HashSet<>();

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal totalEmissions;

    @Builder.Default
    @NotEmpty
    private Set<UUID> noDoubleCountingDeclarationFiles = new HashSet<>();
}
