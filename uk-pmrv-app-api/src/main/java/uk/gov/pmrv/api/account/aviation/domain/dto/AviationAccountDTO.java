package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AviationAccountDTO extends AccountDTO {

    private AviationAccountStatus status;

    private String crcoCode;

    private String closureReason;
    
    private String closedByName;
    
    private LocalDateTime closingDate;

}
