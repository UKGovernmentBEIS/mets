package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.ApprovedAviationAccountQueryService;
import uk.gov.pmrv.api.common.constants.StateConstants;
import uk.gov.pmrv.api.common.exception.BusinessException;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EmissionsMonitoringPlanIdentifierGenerator {

    private final ApprovedAviationAccountQueryService aviationAccountQueryService;

    String generate(Long accountId) {
        AviationAccountInfoDTO accountInfo = aviationAccountQueryService.getApprovedAccountById(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        String authorityCode = accountInfo.getCompetentAuthority().getOneLetterCode();
        String typeCode = accountInfo.getAccountType().getCode();
        String accountIdFormatted = String.format("%05d", accountInfo.getId());

        return String.format("%s-%s-%s-%s", StateConstants.UK, authorityCode, typeCode, accountIdFormatted);
    }
}
