package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftTypeInfo {

    @NotBlank
    @Size(max=10000)
    private String manufacturer;

    @NotBlank
    @Size(max=10000)
    private String model;

    @NotBlank
    @Size(max=10000)
    private String designatorType;
}
