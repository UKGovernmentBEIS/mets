package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;

import java.util.ArrayList;
import java.util.List;

@Schema(
    discriminatorMapping = {
        @DiscriminatorMapping(schema = AirImprovementCalculationCO2.class, value = "CALCULATION_CO2"),
        @DiscriminatorMapping(schema = AirImprovementCalculationPFC.class, value = "CALCULATION_PFC"),
        @DiscriminatorMapping(schema = AirImprovementMeasurement.class, value = "MEASUREMENT_CO2"),
        @DiscriminatorMapping(schema = AirImprovementMeasurement.class, value = "MEASUREMENT_N2O"),
        @DiscriminatorMapping(schema = AirImprovementFallback.class, value = "FALLBACK"),
    },
    discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AirImprovementCalculationCO2.class, name = "CALCULATION_CO2"),
    @JsonSubTypes.Type(value = AirImprovementCalculationPFC.class, name = "CALCULATION_PFC"),
    @JsonSubTypes.Type(value = AirImprovementMeasurement.class, name = "MEASUREMENT_CO2"),
    @JsonSubTypes.Type(value = AirImprovementMeasurement.class, name = "MEASUREMENT_N2O"),
    @JsonSubTypes.Type(value = AirImprovementFallback.class, name = "FALLBACK"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#categoryType eq 'MAJOR') || (#categoryType eq 'MINOR')}")
public abstract class AirImprovement {

    @NotNull
    private MonitoringApproachType type;

    @NotNull
    private CategoryType categoryType;

    @NotEmpty
    @Builder.Default
    private List<String> emissionSources = new ArrayList<>();
}
