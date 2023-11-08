package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAccountReportingStatusHistoryDTO {

    private AviationAccountReportingStatus status;

    private String reason;

    private String submitterName;

    private LocalDateTime submissionDate;
}
