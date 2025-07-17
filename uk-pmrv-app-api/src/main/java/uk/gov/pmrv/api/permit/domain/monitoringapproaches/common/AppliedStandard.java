package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@SpELExpression(expression = "{#deviationFromAppliedStandardExist == (#deviationFromAppliedStandardDetails != null)}", 
    message = "permit.monitoringapproach.common.appliedStandard.deviationFromAppliedStandardExist")
public class AppliedStandard {

    @NotBlank
    @Size(max = 250)
    private String parameter;

    @NotBlank
    @Size(max = 50)
    private String appliedStandard;

    private boolean deviationFromAppliedStandardExist;

    @Size(max = 50)
    private String deviationFromAppliedStandardDetails;
    
    @Valid
    @JsonUnwrapped
    private Laboratory laboratory;

}
