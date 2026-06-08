package uk.gov.pmrv.api.account.aviation.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AviationAccountReportingObligationFirstYearDTO {

    @NotNull
    private LocalDate commencementDate;

    @NotNull
    private String reason;


}
