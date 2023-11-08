package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitiesDescriptionCorsia {

    @NotNull
    private OperatorType operatorType;

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<FlightType> flightTypes = new HashSet<>();

    @NotBlank
    @Size(max = 10000)
    private String activityDescription;
}
