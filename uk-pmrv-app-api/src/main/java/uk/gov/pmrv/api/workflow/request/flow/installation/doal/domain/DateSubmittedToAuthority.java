package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateSubmittedToAuthority {

    @NotNull
    @PastOrPresent
    private LocalDate date;
}
