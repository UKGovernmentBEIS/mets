package uk.gov.pmrv.api.web.orchestrator.account.aviation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAccountEmpDTO {

    private AviationAccountDTO aviationAccount;

    private EmpDetailsDTO emp;

}
