package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NewEntrantType;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NerProceedToAuthorityDetermination extends NerDetermination {

    @NotNull
    private NewEntrantType newEntrantType;

    @NotNull
    @Past
    private LocalDate operationStartDate;

    @Builder.Default
    private List<@Valid @NotNull ActivityLevel> activityLevels = new ArrayList<>();

    @Builder.Default
    private SortedSet<@Valid @NotNull PreliminaryAllocation> preliminaryAllocations = new TreeSet<>();

    @NotNull
    private Boolean sendOfficialNotice;
}
