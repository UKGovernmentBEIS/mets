package uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions;

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
public class AviationAerCorsiaInternationalFlightsEmissionsDetails {

    @JsonUnwrapped
    @NotNull
    @Valid
    private AviationAerCorsiaFlightsEmissionsDetails flightsEmissionsDetails;

    @NotBlank
    @Size(max = 255)
    private String departureState;

    @NotBlank
    @Size(max = 255)
    private String arrivalState;

}
