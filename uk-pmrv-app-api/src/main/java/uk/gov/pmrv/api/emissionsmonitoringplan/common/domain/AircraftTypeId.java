package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class AircraftTypeId implements Serializable {

    @Column(name = "manufacturer")
    @NotBlank
    private String manufacturer;
    @Column(name = "model")
    @NotBlank
    private String model;
    @Column(name = "designatorType")
    @NotBlank
    private String designatorType;
}
