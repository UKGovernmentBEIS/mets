package uk.gov.pmrv.api.reporting.domain.regulatedactivities;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{new java.util.HashSet(#regulatedActivities.![#this.type]).size() eq (#regulatedActivities.size())}", 
    message = "aer.regulated.activities.unique.type")
public class AerRegulatedActivities {

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<AerRegulatedActivity> regulatedActivities = new ArrayList<>();
}
