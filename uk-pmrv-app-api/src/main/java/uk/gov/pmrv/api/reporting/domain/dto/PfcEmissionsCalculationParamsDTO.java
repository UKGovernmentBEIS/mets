package uk.gov.pmrv.api.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.OverVoltageSourceStreamEmissionCalculationMethodData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmissionCalculationMethodData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.SlopeSourceStreamEmissionCalculationMethodData;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PfcEmissionsCalculationParamsDTO {

    @NotNull
    private PFCCalculationMethod calculationMethod;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal totalPrimaryAluminium;

    @Schema(
        discriminatorMapping = {
            @DiscriminatorMapping(schema = SlopeSourceStreamEmissionCalculationMethodData.class, value = "SLOPE"),
            @DiscriminatorMapping(schema = OverVoltageSourceStreamEmissionCalculationMethodData.class, value = "OVERVOLTAGE")

        },
        discriminatorProperty = "calculationMethod")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property =
        "calculationMethod", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = SlopeSourceStreamEmissionCalculationMethodData.class, name =
            "SLOPE"),
        @JsonSubTypes.Type(value = OverVoltageSourceStreamEmissionCalculationMethodData.class, name =
            "OVERVOLTAGE"),
    })
    @Valid
    @NotNull
    private PfcSourceStreamEmissionCalculationMethodData pfcSourceStreamEmissionCalculationMethodData;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PfcEmissionsCalculationParamsDTO that = (PfcEmissionsCalculationParamsDTO) o;

        return ObjectUtils.compare(calculationMethod, that.calculationMethod) == 0
            && pfcSourceStreamEmissionCalculationMethodData.equals(that.pfcSourceStreamEmissionCalculationMethodData)
            && ObjectUtils.compare(totalPrimaryAluminium, that.totalPrimaryAluminium) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(calculationMethod, totalPrimaryAluminium, pfcSourceStreamEmissionCalculationMethodData);
    }

}
