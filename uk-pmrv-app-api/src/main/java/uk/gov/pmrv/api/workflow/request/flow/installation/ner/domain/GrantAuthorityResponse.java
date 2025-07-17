package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;

import java.util.SortedSet;
import java.util.TreeSet;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GrantAuthorityResponse extends AuthorityResponse {

    @Builder.Default
    private SortedSet<@Valid @NotNull PreliminaryAllocation> preliminaryAllocations = new TreeSet<>();

    @NotNull
    @Size(max = 10000)
    private String allocationComments;
}
