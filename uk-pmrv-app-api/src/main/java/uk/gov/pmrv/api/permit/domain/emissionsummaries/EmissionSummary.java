package uk.gov.pmrv.api.permit.domain.emissionsummaries;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#excludedRegulatedActivity != (#regulatedActivity != null)}", message = "permit.emissionSummary.excludedRegulatedActivity")
public class EmissionSummary implements PermitSection {

    @NotBlank
    private String sourceStream;

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionSources = new LinkedHashSet<>();

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionPoints = new LinkedHashSet<>();
    
    private boolean excludedRegulatedActivity;

    @Size(max=1000)
    private String regulatedActivity;

}
