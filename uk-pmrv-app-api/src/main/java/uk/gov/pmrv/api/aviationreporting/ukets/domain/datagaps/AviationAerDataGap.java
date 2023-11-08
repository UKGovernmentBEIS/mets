package uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAerDataGap {

    @NotBlank
    @Size(max = 2000)
    private String reason;

    @NotBlank
    @Size(max = 500)
    private String type;

    @NotBlank
    @Size(max = 2000)
    private String replacementMethod;

    @NotNull
    @Positive
    private Integer flightsAffected;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal totalEmissions;
}
