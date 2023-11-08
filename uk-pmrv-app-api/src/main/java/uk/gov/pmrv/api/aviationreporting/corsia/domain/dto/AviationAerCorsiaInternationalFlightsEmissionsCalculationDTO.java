package uk.gov.pmrv.api.aviationreporting.corsia.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO
        extends AviationAerCorsiaEmissionsCalculationDTO {

    @NotNull
    private Year year;
}
