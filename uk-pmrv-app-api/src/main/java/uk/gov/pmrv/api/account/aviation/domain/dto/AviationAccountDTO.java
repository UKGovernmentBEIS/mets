package uk.gov.pmrv.api.account.aviation.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AviationAccountDTO extends AccountDTO {

    private AviationAccountStatus status;

    private String crcoCode;

    private AviationAccountReportingStatus reportingStatus;

    private String reportingStatusReason;
    
    private String closureReason;
    
    private String closedByName;
    
    private LocalDateTime closingDate;

}
