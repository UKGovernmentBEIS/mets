package uk.gov.pmrv.api.permit.domain.envpermitandlicences;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{#exist == (#envPermitOrLicences?.size() gt 0)}", message = "permit.environmentalPermitsAndLicences.exist")
public class EnvironmentalPermitsAndLicences implements PermitSection {
    
    private boolean exist;
    
    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<EnvPermitOrLicence> envPermitOrLicences;

}