package uk.gov.pmrv.api.integration.registry.accountcreated.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class SetOperatorIdResponseValidator {

    private final AccountQueryService accountQueryService;
    private final PermitQueryService permitQueryService;


    public List<IntegrationEventErrorDetails> validateAviation(OperatorUpdateEvent event) {

        List<IntegrationEventErrorDetails> errors = validateEventDataExistence(event);
        if (!errors.isEmpty()) {
            return errors;
        }

        Account account = accountQueryService.getAccountByEmitterId(event.getEmitterId()).orElse(null);

        if (account == null) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0202)
                .errorMessage(RegistryResponseErrorCode.ERROR_0202.getDescription()).build());
            return errors;
        }

        if(ObjectUtils.isNotEmpty(account.getRegistryId())) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0203)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0203.getDescription()).build());
        }
        Optional<Account> accountWithSameRegistryId = accountQueryService.getAccountByRegistryId(event.getOperatorId().intValue());
        if(accountWithSameRegistryId.isPresent() && !accountWithSameRegistryId.get().getEmitterId().equals(event.getEmitterId())) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0204)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0204.getDescription()).build());
        }
        EmissionTradingScheme emissionTradingScheme = account.getEmissionTradingScheme();
        if(emissionTradingScheme == null || !emissionTradingScheme.equals(EmissionTradingScheme.UK_ETS_AVIATION)) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0205)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0205.getDescription()).build());
            return errors;
        }
        if(account.getStatus().equals(AviationAccountStatus.CLOSED)) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0205)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0205.getDescription()).build());
            return errors;
        }

        return errors;
    }

    public List<IntegrationEventErrorDetails> validateInstallation(OperatorUpdateEvent event) {

        List<IntegrationEventErrorDetails> errors = validateEventDataExistence(event);
        if (!errors.isEmpty()) {
            return errors;
        }

        Account account = accountQueryService.getAccountByEmitterId(event.getEmitterId()).orElse(null);

        if (account == null) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0202)
                .errorMessage(RegistryResponseErrorCode.ERROR_0202.getDescription()).build());
            return errors;
        }

        if(ObjectUtils.isNotEmpty(account.getRegistryId())) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0203)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0203.getDescription()).build());
        }
        Optional<Account> accountWithSameRegistryId = accountQueryService.getAccountByRegistryId(event.getOperatorId().intValue());
        if(accountWithSameRegistryId.isPresent() && !accountWithSameRegistryId.get().getEmitterId().equals(event.getEmitterId())
                && accountWithSameRegistryId.get().getStatus().equals(InstallationAccountStatus.LIVE)) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0204)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0204.getDescription()).build());
        }
        EmissionTradingScheme emissionTradingScheme = account.getEmissionTradingScheme();
        if(emissionTradingScheme == null || !emissionTradingScheme.equals(EmissionTradingScheme.UK_ETS_INSTALLATIONS)) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0205)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0205.getDescription()).build());
            return errors;
        }
        try {
            PermitContainer permitContainer = permitQueryService.getPermitContainerByAccountId(account.getId());
            if(!permitContainer.getPermitType().equals(PermitType.GHGE)) {
                errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0205)
                        .errorMessage(RegistryResponseErrorCode.ERROR_0205.getDescription()).build());
                return errors;
            }
            if(!account.getStatus().equals(InstallationAccountStatus.LIVE)) {
                errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0205)
                        .errorMessage(RegistryResponseErrorCode.ERROR_0205.getDescription()).build());
                return errors;
            }
        } catch (BusinessException e) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0205)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0205.getDescription()).build());
            return errors;
        }

        return errors;
    }

    private List<IntegrationEventErrorDetails> validateEventDataExistence(OperatorUpdateEvent event) {
        List<IntegrationEventErrorDetails> errors = new ArrayList<>();
        if (ObjectUtils.isEmpty(event.getEmitterId()) || ObjectUtils.isEmpty(event.getOperatorId()) ||
            ObjectUtils.isEmpty(event.getRegulator())) {
            errors.add(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0201)
                .errorMessage("Invalid payload for the operator id process ").build());
        }
        return errors;
    }

}
