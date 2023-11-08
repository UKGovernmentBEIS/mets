package uk.gov.pmrv.api.permit.domain.monitoringapproaches;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.PermitSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransferredCO2AndN2OMonitoringApproach;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = CalculationOfCO2MonitoringApproach.class, value = "CALCULATION"),
                @DiscriminatorMapping(schema = MeasurementOfCO2MonitoringApproach.class, value = "MEASUREMENT"),
                @DiscriminatorMapping(schema = FallbackMonitoringApproach.class, value = "FALLBACK"),
                @DiscriminatorMapping(schema = MeasurementOfN2OMonitoringApproach.class, value = "N2O"),
                @DiscriminatorMapping(schema = CalculationOfPFCMonitoringApproach.class, value = "PFC"),
                @DiscriminatorMapping(schema = InherentCO2MonitoringApproach.class, value = "INHERENT_CO2"),
                @DiscriminatorMapping(schema = TransferredCO2AndN2OMonitoringApproach.class, value = "TRANSFERRED_CO2")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CalculationOfCO2MonitoringApproach.class, name = "CALCULATION_CO2"),
    @JsonSubTypes.Type(value = MeasurementOfCO2MonitoringApproach.class, name = "MEASUREMENT_CO2"),
    @JsonSubTypes.Type(value = FallbackMonitoringApproach.class, name = "FALLBACK"),
    @JsonSubTypes.Type(value = MeasurementOfN2OMonitoringApproach.class, name = "MEASUREMENT_N2O"),
    @JsonSubTypes.Type(value = CalculationOfPFCMonitoringApproach.class, name = "CALCULATION_PFC"),
    @JsonSubTypes.Type(value = InherentCO2MonitoringApproach.class, name = "INHERENT_CO2"),
    @JsonSubTypes.Type(value = TransferredCO2AndN2OMonitoringApproach.class, name = "TRANSFERRED_CO2_N2O")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class PermitMonitoringApproachSection implements PermitSection {

    private MonitoringApproachType type;
    
    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return Collections.unmodifiableSet(new HashSet<>());
    }

    @JsonIgnore
    public void clearAttachmentIds() {
    }
}
