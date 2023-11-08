package uk.gov.pmrv.api.account.aviation.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAccountReportingStatusHistoryCreationDTO {

    @NotNull
    private AviationAccountReportingStatus status;

    @NotBlank
    @Size(max = 2000)
    private String reason;
}
