package uk.gov.pmrv.api.aviationreporting.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AviationAerValidatorService {

    private final AviationAccountQueryService accountQueryService;
    private final List<AviationAerTradingSchemeValidatorService<? extends AviationAerContainer>> aerTradingSchemeValidatorServices;

    @SuppressWarnings("unchecked")
    public void validate(Long accountId, AviationAerContainer aerContainer) {
        EmissionTradingScheme emissionTradingScheme = accountQueryService.getAviationAccountInfoDTOById(accountId).getEmissionTradingScheme();
        getValidatorService(emissionTradingScheme).validate(aerContainer);
    }

    private AviationAerTradingSchemeValidatorService getValidatorService(EmissionTradingScheme emissionTradingScheme) {
        return aerTradingSchemeValidatorServices.stream()
            .filter(empTradingSchemeValidatorService -> emissionTradingScheme.equals(empTradingSchemeValidatorService.getEmissionTradingScheme()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "No suitable validator found"));
    }
}
