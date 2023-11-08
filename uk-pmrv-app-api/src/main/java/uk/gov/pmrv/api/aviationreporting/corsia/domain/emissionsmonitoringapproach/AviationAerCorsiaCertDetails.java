package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerCorsiaCertDetails {

    @NotNull
    private AviationAerCorsiaFlightType flightType;

    @NotNull
    private Year publicationYear;
}
