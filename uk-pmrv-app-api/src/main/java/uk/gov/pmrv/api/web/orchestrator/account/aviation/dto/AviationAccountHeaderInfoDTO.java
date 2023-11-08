package uk.gov.pmrv.api.web.orchestrator.account.aviation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.web.orchestrator.account.AccountHeaderInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AviationAccountHeaderInfoDTO extends AccountHeaderInfoDTO {

    private AviationAccountStatus status;
    private EmissionTradingScheme emissionTradingScheme;
    private String empId;
}
