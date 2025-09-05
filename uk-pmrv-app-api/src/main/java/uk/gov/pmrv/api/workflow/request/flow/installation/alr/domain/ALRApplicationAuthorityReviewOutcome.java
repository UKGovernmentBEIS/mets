package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ALRApplicationAuthorityReviewOutcome {

    @NotNull
    private ALR alr;

    @PastOrPresent
    private LocalDate submissionDate;

    @Valid
    @NotNull
    private ALRAuthorityResponse authorityResponse;
}
