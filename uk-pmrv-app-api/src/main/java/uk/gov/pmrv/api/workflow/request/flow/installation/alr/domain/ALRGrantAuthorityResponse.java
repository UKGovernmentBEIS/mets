package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Year;
import java.util.UUID;
import java.util.SortedMap;
import java.util.Set;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ALRGrantAuthorityResponse extends ALRAuthorityResponse {

    @Builder.Default
    private SortedSet<ALRPreliminaryAllocation> preliminaryAllocations = new TreeSet<>();

    @Builder.Default
    private SortedMap<Year, Integer> totalAllocationsPerYear = new TreeMap<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> documents = new HashSet<>();
}
