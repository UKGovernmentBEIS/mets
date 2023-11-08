package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FallbackEmissions extends AerMonitoringApproachEmissions {

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> sourceStreams = new LinkedHashSet<>();

    @NotNull
    @Valid
    private FallbackBiomass biomass;

    @NotBlank
    private String description;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalFossilEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalFossilEnergyContent;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal reportableEmissions;

    @Override
    public Set<UUID> getAttachmentIds() {
        return this.files;
    }
}
