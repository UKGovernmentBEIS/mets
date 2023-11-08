package uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAerAircraftDataDetails {

    @NotBlank
    private String aircraftTypeDesignator;

    @Size(max = 255)
    private String subType;

    @NotBlank
    @Size(max = 20)
    private String registrationNumber;

    @NotBlank
    @Size(max = 255)
    private String ownerOrLessor;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
