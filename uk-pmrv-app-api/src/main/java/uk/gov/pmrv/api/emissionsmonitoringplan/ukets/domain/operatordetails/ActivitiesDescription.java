package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitiesDescription {

    @NotNull
    private OperatorType operatorType;

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<FlightType> flightTypes = new HashSet<>();

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<OperationScope> operationScopes = new HashSet<>();

    @NotBlank
    @Size(max = 10000)
    private String activityDescription;
}
