package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AviationAccountRegistryManualPushAvailabilityService {

    private final AviationAccountQueryService aviationAccountQueryService;
    private final RequestService requestService;

    @Value("${registry.integration.account.creation.enabled}")
    private boolean isAccountCreateEnabled;

    @Transactional(readOnly = true)
    public Boolean isManualPushAvailable(String requestId) {

        if(!isAccountCreateEnabled) {
            return Boolean.FALSE;
        }

        Request request = requestService.findRequestById(requestId);
        AviationAccountDTO aviationAccountDTO = aviationAccountQueryService.getAviationAccountDTOById(request.getAccountId());

        EmissionTradingScheme tradingScheme = aviationAccountDTO.getEmissionTradingScheme();
        RequestStatus requestStatus = request.getStatus();
        Integer registryId = aviationAccountDTO.getRegistryId();

        return EmissionTradingScheme.UK_ETS_AVIATION.equals(tradingScheme) &&
                Objects.isNull(registryId) && RequestStatus.IN_PROGRESS.equals(requestStatus);

    }

}
