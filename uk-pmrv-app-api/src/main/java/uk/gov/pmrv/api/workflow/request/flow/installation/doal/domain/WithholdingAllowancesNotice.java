package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithholdingAllowancesNotice {

    @PastOrPresent
    private LocalDate noticeIssuedDate;

    @Size(max = 10000)
    @NotBlank
    private String withholdingOfAllowancesComment;
}
