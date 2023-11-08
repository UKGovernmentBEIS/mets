package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certificateExist) == (#certificateNumber != null)}", message = "emp.operatorDetails.airOperatingDetails.certificateNumber")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certificateExist) == (#issuingAuthority != null)}", message = "emp.operatorDetails.airOperatingDetails.issuingAuthority")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certificateExist) == (#certificateFiles?.size() gt 0)}", message = "emp.operatorDetails.airOperatingDetails.certificateFiles")
public class AirOperatingCertificate {

    @NotNull
    private Boolean certificateExist;

    @Size(max = 255)
    private String certificateNumber;

    @Size(max = 255)
    private String issuingAuthority;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> certificateFiles = new HashSet<>();
}
