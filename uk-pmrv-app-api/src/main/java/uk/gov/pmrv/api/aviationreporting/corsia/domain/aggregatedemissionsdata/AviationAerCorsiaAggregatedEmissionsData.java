package uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElements;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAerCorsiaAggregatedEmissionsData {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    @UniqueElements
    private Set<@NotNull @Valid AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails = new HashSet<>();
}
