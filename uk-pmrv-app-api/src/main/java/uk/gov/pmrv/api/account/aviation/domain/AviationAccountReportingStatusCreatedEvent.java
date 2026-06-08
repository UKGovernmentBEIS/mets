package uk.gov.pmrv.api.account.aviation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAccountReportingStatusCreatedEvent {

    private Long accountId;
    private Year year;
    private EmissionTradingScheme emissionTradingScheme;


}
