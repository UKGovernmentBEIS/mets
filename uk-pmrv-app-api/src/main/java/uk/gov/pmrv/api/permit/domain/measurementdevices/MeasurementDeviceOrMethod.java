package uk.gov.pmrv.api.permit.domain.measurementdevices;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitIdSection;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'OTHER') == (#otherTypeName != null)}", message = "permit.measurementDeviceOrMethod.otherTypeName")
@SpELExpression(expression = "{#uncertaintySpecified == (#specifiedUncertaintyPercentage != null)}", message = "permit.measurementDeviceOrMethod.uncertaintySpecified")
public class MeasurementDeviceOrMethod extends PermitIdSection {

    //TODO revisit validation
    @Size(max = 10000)
    @NotBlank
    private String reference;

    @NotNull
    private MeasurementDeviceType type;

    //TODO revisit validation
    @Size(max = 10000)
    private String otherTypeName;

    //TODO revisit validation
    @Size(max = 10000)
    @NotBlank
    private String measurementRange;

    //TODO revisit validation
    @Size(max = 10000)
    @NotBlank
    private String meteringRangeUnits;

    private boolean uncertaintySpecified;

    @DecimalMin(value = "0")
    @Digits(integer = 2, fraction = 3)
    private BigDecimal specifiedUncertaintyPercentage;

    //TODO revisit validation
    @Size(max = 10000)
    @NotBlank
    private String location;

}
