package uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = AviationAerSmallEmittersMonitoringApproach.class, value = "EUROCONTROL_SMALL_EMITTERS"),
                @DiscriminatorMapping(schema = AviationAerSupportFacilityMonitoringApproach.class, value = "EUROCONTROL_SUPPORT_FACILITY"),
                @DiscriminatorMapping(schema = AviationAerFuelMonitoringApproach.class, value = "FUEL_USE_MONITORING")
        },
        discriminatorProperty = "monitoringApproachType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "monitoringApproachType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AviationAerSmallEmittersMonitoringApproach.class, name = "EUROCONTROL_SMALL_EMITTERS"),
        @JsonSubTypes.Type(value = AviationAerSupportFacilityMonitoringApproach.class, name = "EUROCONTROL_SUPPORT_FACILITY"),
        @JsonSubTypes.Type(value = AviationAerFuelMonitoringApproach.class, name = "FUEL_USE_MONITORING")
})

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AviationAerEmissionsMonitoringApproach {

    @NotNull
    private EmissionsMonitoringApproachType monitoringApproachType;
}
