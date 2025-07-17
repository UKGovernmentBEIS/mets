package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
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
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElements;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpEmissionSourcesCorsia implements EmpCorsiaSection {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    @UniqueElements
    private Set<@NotNull @Valid AircraftTypeDetailsCorsia> aircraftTypes = new HashSet<>();

    @Size(max = 10000)
    private String multipleFuelConsumptionMethodsExplanation;
}
