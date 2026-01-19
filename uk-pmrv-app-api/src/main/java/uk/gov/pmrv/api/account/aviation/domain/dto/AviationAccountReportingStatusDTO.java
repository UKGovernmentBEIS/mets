package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAccountReportingStatusDTO {

    private Long accountId;
    private AviationAccountReportingStatusType status;
    private Year year;
    private LocalDateTime lastUpdate;
    private Boolean isReported;

}
