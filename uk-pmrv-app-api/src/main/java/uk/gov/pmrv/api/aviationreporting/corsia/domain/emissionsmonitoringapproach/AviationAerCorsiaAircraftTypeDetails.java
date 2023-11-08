package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerCorsiaAircraftTypeDetails {

    @NotBlank
    @Size(max = 255)
    private String designator;

    @Size(max = 255)
    private String subtype;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal fuelBurnRatio;

}
