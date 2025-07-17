package uk.gov.pmrv.api.web.orchestrator.account.aviation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.transform.AviationAccountHeaderInfoMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationAccountEmpQueryOrchestrator {

    private final AviationAccountQueryService aviationAccountQueryService;
    private final EmissionsMonitoringPlanQueryService empQueryService;
    private static final AviationAccountHeaderInfoMapper ACCOUNT_HEADER_INFO_MAPPER = Mappers.getMapper(AviationAccountHeaderInfoMapper .class);

    public AviationAccountEmpDTO getAviationAccountWithEMP(Long accountId, AppUser appUser) {
        final AviationAccountDTO aviationAccountDTO = aviationAccountQueryService.getAviationAccountDTOByIdAndUser(accountId, appUser);
        final Optional<EmpDetailsDTO> empDetailsDTO = empQueryService.getEmissionsMonitoringPlanDetailsDTOByAccountId(accountId);

        return AviationAccountEmpDTO.builder()
                .aviationAccount(aviationAccountDTO)
                .emp(empDetailsDTO.orElse(null))
                .build();

    }

    @Transactional(readOnly = true)
    public AviationAccountHeaderInfoDTO getAccountHeaderInfo(Long accountId) {
        AviationAccountInfoDTO accountInfo = aviationAccountQueryService.getAviationAccountInfoDTOById(accountId);
        String empId = empQueryService.getEmpIdByAccountId(accountId).orElse(null);

        return ACCOUNT_HEADER_INFO_MAPPER.toAccountHeaderInfoDTO(accountInfo, empId);
    }
}
