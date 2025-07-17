package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certificateExist) == (#certificateNumber != null)}", message = "emp.operatorDetails.airOperatingDetails.certificateNumber")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certificateExist) == (#issuingAuthority != null)}", message = "emp.operatorDetails.airOperatingDetails.issuingAuthority")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certificateExist) == (#certificateFiles?.size() gt 0)}", message = "emp.operatorDetails.airOperatingDetails.certificateFiles")
@SpELExpression(expression = "(T(java.lang.Boolean).TRUE.equals(#certificateExist) && #restrictionsExist != null) || (!T(java.lang.Boolean).TRUE.equals(#certificateExist) && (#restrictionsExist == null || !T(java.lang.Boolean).TRUE.equals(#restrictionsExist)))", message = "emp.operatorDetails.airOperatingDetails.restrictionsExist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#restrictionsExist) == (#restrictionsDetails != null)}", message = "emp.operatorDetails.airOperatingDetails.restrictionsDetails")

public class AirOperatingCertificateCorsia {

    @NotNull
    private Boolean certificateExist;

    @Size(max = 255)
    private String certificateNumber;

    @Size(max = 255)
    private String issuingAuthority;

    private Boolean restrictionsExist;

    @Size(max = 500)
    private String restrictionsDetails;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> certificateFiles = new HashSet<>();
}
