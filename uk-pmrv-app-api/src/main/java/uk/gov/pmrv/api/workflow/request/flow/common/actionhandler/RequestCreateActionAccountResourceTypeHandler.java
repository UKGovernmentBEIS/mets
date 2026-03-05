package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RequestCreateActionAccountResourceTypeHandler<T extends RequestCreateActionPayload> implements RequestCreateActionResourceTypeHandler<T> {
    private final List<RequestCreateByAccountValidator> requestCreateByAccountValidators;
    private final List<RequestCreateByRequestValidator> requestCreateByRequestValidators;
    private final List<RequestAccountCreateActionHandler<T>> requestAccountCreateActionHandlers;
    private final AccountQueryService accountQueryService;

    @Override
    public String process(String resourceId, RequestCreateActionType requestCreateActionType, T payload, AppUser appUser) {
        final Long accountId = resourceId != null ? Long.parseLong(resourceId) : null;

        if(resourceId != null || RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.equals(requestCreateActionType)) {
            Optional<RequestCreateByAccountValidator> requestCreateByAccountValidatorOpt = requestCreateByAccountValidators
                    .stream().filter(requestCreateValidator -> requestCreateValidator.getType().equals(requestCreateActionType)).findFirst();
            Optional<RequestCreateByRequestValidator> requestCreateByRequestValidatorOpt = requestCreateByRequestValidators
                    .stream().filter(requestCreateValidator -> requestCreateValidator.getType().equals(requestCreateActionType)).findFirst();

            if(requestCreateByAccountValidatorOpt.isEmpty() && requestCreateByRequestValidatorOpt.isEmpty()) {
                return null;
            }

            if(resourceId != null) {
                AccountType accountType = accountQueryService.getAccountType(accountId);
                EmissionTradingScheme emissionTradingScheme = accountQueryService.getAccountEmissionTradingScheme(accountId);

                Set<RequestType> availableForAccountCreateRequestTypes = RequestType.getAvailableForAccountCreateRequestTypes(accountType, emissionTradingScheme);

                Set<RequestType> availableForAERCreateRequestTypes = RequestType.getAvailableForAERCreateRequestTypes(accountType, emissionTradingScheme);

                Set<RequestType> availableForBDRCreateRequestTypes = RequestType.getAvailableForBDRCreateRequestTypes(accountType);

                Set<RequestType> allAvailableRequests = Stream.of(availableForAccountCreateRequestTypes,availableForAERCreateRequestTypes,availableForBDRCreateRequestTypes)
                        .flatMap(Set::stream).collect(Collectors.toSet());

                if(!allAvailableRequests.contains(requestCreateActionType.getType())) {
                    throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED,
                            String.format("%s is not supported for accounts of type %s", requestCreateActionType, accountType));
                }
                // lock the account
                accountQueryService.exclusiveLockAccount(accountId);
            }

            final RequestCreateValidationResult validationResult = requestCreateByAccountValidatorOpt
                    .map(requestCreateByAccountValidator -> requestCreateByAccountValidator.validateAction(accountId))
                    .orElse(requestCreateByRequestValidatorOpt
                            .map(requestCreateByRequestValidator -> requestCreateByRequestValidator.validateAction(accountId, payload))
                            .orElse(RequestCreateValidationResult.builder().valid(true).isAvailable(true).build())
                    );

            if(!validationResult.isValid() || !validationResult.isAvailable()) {
                throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, validationResult);
            }
        }

        return requestAccountCreateActionHandlers.stream()
                .filter(handler -> handler.getRequestCreateActionType().equals(requestCreateActionType))
                .findFirst()
                .map(handler -> handler.process(accountId, payload, appUser))
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestCreateActionType));
    }

    @Override
    public String getResourceType() {
        return ResourceType.ACCOUNT;
    }
}
