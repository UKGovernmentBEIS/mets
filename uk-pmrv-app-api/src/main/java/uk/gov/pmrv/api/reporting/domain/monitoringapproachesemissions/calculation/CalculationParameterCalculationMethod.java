package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = CalculationManualCalculationMethod.class, value = "MANUAL"),
                @DiscriminatorMapping(schema = CalculationRegionalDataCalculationMethod.class, value = "REGIONAL_DATA"),
                @DiscriminatorMapping(schema = CalculationNationalInventoryDataCalculationMethod.class, value = "NATIONAL_INVENTORY_DATA")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CalculationManualCalculationMethod.class, name = "MANUAL"),
    @JsonSubTypes.Type(value = CalculationRegionalDataCalculationMethod.class, name = "REGIONAL_DATA"),
    @JsonSubTypes.Type(value = CalculationNationalInventoryDataCalculationMethod.class, name = "NATIONAL_INVENTORY_DATA")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class CalculationParameterCalculationMethod {

    @NotNull
    private CalculationParameterCalculationMethodType type;

    @Valid
    @NotNull
    private CalculationActivityDataCalculationMethod calculationActivityDataCalculationMethod;

    public abstract CalculationEmissionCalculationParamValues getEmissionCalculationParamValues();
}
