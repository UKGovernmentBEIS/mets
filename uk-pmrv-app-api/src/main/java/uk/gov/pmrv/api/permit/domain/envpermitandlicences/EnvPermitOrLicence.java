package uk.gov.pmrv.api.permit.domain.envpermitandlicences;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type?.length() gt 0 || #num?.length() gt 0 || #issuingAuthority?.length() gt 0 || #permitHolder?.length() gt 0)}",
    message = "permit.environmentalPermitsAndLicences.at.least.one.field.required")
public class EnvPermitOrLicence {

    @Size(max=1000)
    private String type;

    @Size(max=1000)
    private String num;

    @Size(max=1000)
    private String issuingAuthority;

    @Size(max=1000)
    private String permitHolder;
}
