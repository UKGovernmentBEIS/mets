package uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerCorsiaInternationalFlightsEmissions {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid AviationAerCorsiaInternationalFlightsEmissionsDetails> flightsEmissionsDetails = new ArrayList<>();

}
