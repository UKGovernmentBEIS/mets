package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = CertMonitoringApproach.class, value = "CERT_MONITORING"),
                @DiscriminatorMapping(schema = FuelMonitoringApproach.class, value = "FUEL_USE_MONITORING")
        },
        discriminatorProperty = "monitoringApproachType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "monitoringApproachType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CertMonitoringApproach.class, name = "CERT_MONITORING"),
        @JsonSubTypes.Type(value = FuelMonitoringApproach.class, name = "FUEL_USE_MONITORING")
})

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class EmpEmissionsMonitoringApproachCorsia implements EmpCorsiaSection {

    @NotNull
    private EmissionsMonitoringApproachTypeCorsia monitoringApproachType;

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return Collections.unmodifiableSet(new HashSet<>());
    }

}
