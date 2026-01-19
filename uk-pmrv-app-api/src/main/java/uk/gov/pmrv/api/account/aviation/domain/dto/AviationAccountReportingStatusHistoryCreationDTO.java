package uk.gov.pmrv.api.account.aviation.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;

import java.time.Year;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAccountReportingStatusHistoryCreationDTO {

    @NotNull
    private AviationAccountReportingStatusType status;

    @NotNull
    private Year year;

    @NotBlank
    @Size(max = 2000)
    private String reason;
}
