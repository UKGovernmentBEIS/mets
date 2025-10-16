package uk.gov.pmrv.api.integration.registry.accountcreated.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.setoperator.common.RegistryIntegrationEventError;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdResponseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class SetOperatorIdResponseValidator {

    private final AccountQueryService accountQueryService;


    public List<RegistryIntegrationEventError> validate(SetOperatorIdResponseEvent event) {
        List<RegistryIntegrationEventError> errors = new ArrayList<>();

        String emitterId = event.getEmitterId();
        if(ObjectUtils.isEmpty(emitterId) || ObjectUtils.isEmpty(event.getOperatorId())) {
            errors.add(RegistryIntegrationEventError.builder().error(RegistryResponseErrorCode.ERROR_0201)
                    .errorMessage("Invalid payload for the operator id process ").build());
            return errors;
        }
        Optional<Account> accountOptional = accountQueryService.getAccountByEmitterId(event.getEmitterId());
        if(accountOptional.isEmpty()) {
            errors.add(RegistryIntegrationEventError.builder().error(RegistryResponseErrorCode.ERROR_0202)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0202.getDescription()).build());
            return errors;
        }
        Account account = accountOptional.get();
        if(ObjectUtils.isNotEmpty(account.getRegistryId())) {
            errors.add(RegistryIntegrationEventError.builder().error(RegistryResponseErrorCode.ERROR_0203)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0203.getDescription()).build());
        }
        Optional<Account> accountWithSameRegistryId = accountQueryService.getAccountByRegistryId(event.getOperatorId());
        if(accountWithSameRegistryId.isPresent() && accountWithSameRegistryId.get().getStatus().getName().equals("LIVE")) {
            errors.add(RegistryIntegrationEventError.builder().error(RegistryResponseErrorCode.ERROR_0204)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0204.getDescription()).build());
        }
        EmissionTradingScheme emissionTradingScheme = account.getEmissionTradingScheme();
        if(emissionTradingScheme == null || !(emissionTradingScheme.equals(EmissionTradingScheme.UK_ETS_AVIATION)
        || emissionTradingScheme.equals(EmissionTradingScheme.UK_ETS_INSTALLATIONS))) {
            errors.add(RegistryIntegrationEventError.builder().error(RegistryResponseErrorCode.ERROR_0205)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0205.getDescription()).build());
        }

        return errors;
    }



}
