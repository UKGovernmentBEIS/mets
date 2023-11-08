package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;

import java.math.BigDecimal;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = CalculationActivityDataContinuousMeteringCalcMethod.class, value = "CONTINUOUS_METERING"),
                @DiscriminatorMapping(schema = CalculationActivityDataAggregationMeteringCalcMethod.class, value = "AGGREGATION_OF_METERING_QUANTITIES")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CalculationActivityDataContinuousMeteringCalcMethod.class, name = "CONTINUOUS_METERING"),
    @JsonSubTypes.Type(value = CalculationActivityDataAggregationMeteringCalcMethod.class, name = "AGGREGATION_OF_METERING_QUANTITIES"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class CalculationActivityDataCalculationMethod {

    @NotNull
    private CalculationActivityDataCalculationMethodType type;

    @NotNull
    private ActivityDataMeasurementUnit measurementUnit;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    @NotNull
    private BigDecimal totalMaterial;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    @NotNull
    private BigDecimal activityData;
}
