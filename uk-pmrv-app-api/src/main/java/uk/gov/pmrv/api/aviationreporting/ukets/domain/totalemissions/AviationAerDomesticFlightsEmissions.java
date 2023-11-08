package uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerDomesticFlightsEmissions {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid AviationAerDomesticFlightsEmissionsDetails> domesticFlightsEmissionsDetails = new ArrayList<>();

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @PositiveOrZero
    private BigDecimal totalEmissions;
}
