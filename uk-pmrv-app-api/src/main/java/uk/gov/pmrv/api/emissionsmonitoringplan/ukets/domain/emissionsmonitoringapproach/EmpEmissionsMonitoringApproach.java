package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = SmallEmittersMonitoringApproach.class, value = "EUROCONTROL_SMALL_EMITTERS"),
                @DiscriminatorMapping(schema = SupportFacilityMonitoringApproach.class, value = "EUROCONTROL_SUPPORT_FACILITY"),
                @DiscriminatorMapping(schema = FuelMonitoringApproach.class, value = "FUEL_USE_MONITORING")
        },
        discriminatorProperty = "monitoringApproachType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "monitoringApproachType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SmallEmittersMonitoringApproach.class, name = "EUROCONTROL_SMALL_EMITTERS"),
        @JsonSubTypes.Type(value = SupportFacilityMonitoringApproach.class, name = "EUROCONTROL_SUPPORT_FACILITY"),
        @JsonSubTypes.Type(value = FuelMonitoringApproach.class, name = "FUEL_USE_MONITORING")
})

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class EmpEmissionsMonitoringApproach implements EmpUkEtsSection {

    @NotNull
    private EmissionsMonitoringApproachType monitoringApproachType;

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return Collections.unmodifiableSet(new HashSet<>());
    }

}
