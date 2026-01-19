package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAccountReportingStatusHistoryDTO {

    private AviationAccountReportingStatusType status;

    private String reason;

    private Year year;

    private String submitterName;

    private LocalDateTime submissionDate;
}
