package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#licenseExist) == (#licenseNumber != null)}", message = "emp.operatorDetails.operatingLicense.licenseNumber")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#licenseExist) == (#issuingAuthority != null)}", message = "emp.operatorDetails.operatingLicense.issuingAuthority")
public class OperatingLicense {

    @NotNull
    private Boolean licenseExist;

    @Size(max = 255)
    private String licenseNumber;

    @Size(max = 255)
    private String issuingAuthority;
}
