package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;

import java.time.Year;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DoalGrantAuthorityResponse extends DoalAuthorityResponse {

    @Builder.Default
    private SortedSet<PreliminaryAllocation> preliminaryAllocations = new TreeSet<>();

    @Builder.Default
    private SortedMap<Year, Integer> totalAllocationsPerYear = new TreeMap<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> documents;
}
