package uk.gov.pmrv.api.account.aviation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AviationAccountCreatedEvent {

    private Long accountId;
    private EmissionTradingScheme emissionTradingScheme;
}
