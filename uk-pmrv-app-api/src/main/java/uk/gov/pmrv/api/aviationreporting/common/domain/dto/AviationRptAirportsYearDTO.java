package uk.gov.pmrv.api.aviationreporting.common.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationRptAirportsYearDTO {

    @NotEmpty
    private Set<String> icaos;

    @NotNull
    private Year year;

}
