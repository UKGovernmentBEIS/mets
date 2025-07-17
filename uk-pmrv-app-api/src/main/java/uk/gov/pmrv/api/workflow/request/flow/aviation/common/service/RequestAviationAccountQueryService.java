package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

@Service
@RequiredArgsConstructor
public class RequestAviationAccountQueryService {

    private final AccountContactQueryService accountContactQueryService;
    private final AviationAccountQueryService aviationAccountQueryService;

    @Transactional(readOnly = true)
    public RequestAviationAccountInfo getAccountInfo(Long accountId) {
        ServiceContactDetails accountServiceContactDetails = accountContactQueryService
            .getServiceContactDetails(accountId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        AviationAccountInfoDTO accountInfo = aviationAccountQueryService.getAviationAccountInfoDTOById(accountId);

        return RequestAviationAccountInfo.builder()
            .operatorName(accountInfo.getName())
            .crcoCode(accountInfo.getCrcoCode())
            .serviceContactDetails(accountServiceContactDetails)
            .build();
    }
}
