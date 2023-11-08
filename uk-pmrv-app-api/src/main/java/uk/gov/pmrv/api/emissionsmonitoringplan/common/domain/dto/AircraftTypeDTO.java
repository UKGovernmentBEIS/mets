package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AircraftTypeDTO {

    @Size(max = 255)
    @NotBlank
    private String manufacturer;

    @Size(max = 255)
    @NotBlank
    private String model;

    @Size(max = 255)
    @NotBlank
    private String designatorType;
}
