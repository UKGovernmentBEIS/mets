package uk.gov.pmrv.api.aviationreporting.common.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AviationRptAirportsDTO {

    @NotBlank
    @Size(max = 255)
    @EqualsAndHashCode.Include()
    private String icao;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String country;

    @NotNull
    private CountryType countryType;

    @NotBlank
    @Size(max = 255)
    private String state;
}
