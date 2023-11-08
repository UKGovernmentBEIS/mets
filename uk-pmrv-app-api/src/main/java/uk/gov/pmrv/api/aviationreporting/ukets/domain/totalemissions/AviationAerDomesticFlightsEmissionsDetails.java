package uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerDomesticFlightsEmissionsDetails {

    @JsonUnwrapped
    @NotNull
    @Valid
    private AviationAerFlightsEmissionsDetails flightsEmissionsDetails;

    @NotBlank
    @Size(max = 255)
    private String country;

}
