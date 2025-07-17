package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueField;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.enumeration.FuelTypeCorsia;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftTypeDetailsCorsia {

    @NotNull
    @Valid
    @UniqueField
    private AircraftTypeInfo aircraftTypeInfo;

    @Size(max = 10000)
    private String subtype;

    @NotNull
    @Min(0)
    @Max(9999999999L)
    private Long numberOfAircrafts;

    @Builder.Default
    @NotEmpty
    private List<@NotNull FuelTypeCorsia> fuelTypes = new ArrayList<>();

    private FuelConsumptionMeasuringMethod fuelConsumptionMeasuringMethod;

}
