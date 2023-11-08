package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(
    discriminatorMapping = {
        @DiscriminatorMapping(schema = SlopeSourceStreamEmissionCalculationMethodData.class, value = "SLOPE"),
        @DiscriminatorMapping(schema = OverVoltageSourceStreamEmissionCalculationMethodData.class, value =
            "OVERVOLTAGE")

    },
    discriminatorProperty = "calculationMethod")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property =
    "calculationMethod", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SlopeSourceStreamEmissionCalculationMethodData.class, name =
        "SLOPE"),
    @JsonSubTypes.Type(value = OverVoltageSourceStreamEmissionCalculationMethodData.class, name =
        "OVERVOLTAGE"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PfcSourceStreamEmissionCalculationMethodData {

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal c2F6WeightFraction;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "100.00000")
    @Digits(integer = 3, fraction = 5)
    private BigDecimal percentageOfCollectionEfficiency;

    @NotNull
    private PFCCalculationMethod calculationMethod;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PfcSourceStreamEmissionCalculationMethodData that = (PfcSourceStreamEmissionCalculationMethodData) o;

        return ObjectUtils.compare(c2F6WeightFraction, that.c2F6WeightFraction) == 0
            && ObjectUtils.compare(percentageOfCollectionEfficiency, that.percentageOfCollectionEfficiency) == 0
            && calculationMethod == that.calculationMethod;
    }


    @Override
    public int hashCode() {
        return Objects.hash(c2F6WeightFraction, percentageOfCollectionEfficiency, calculationMethod);
    }

}
