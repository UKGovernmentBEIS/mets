package uk.gov.pmrv.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.resource.AccountRequestAuthorizationResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.validation.EnabledWorkflowValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AvailableRequestService {

    private final RequestRepository requestRepository;
    private final AccountRequestAuthorizationResourceService accountRequestAuthorizationResourceService;
    private final List<RequestCreateByAccountValidator> requestCreateByAccountValidators;
    private final List<RequestCreateByRequestValidator> requestCreateByRequestValidators;
    private final EnabledWorkflowValidator enabledWorkflowValidator;
    private final AccountQueryService accountQueryService;

    @Transactional
    public Map<RequestCreateActionType, RequestCreateValidationResult> getAvailableAccountWorkflows(final Long accountId, final AppUser appUser) {

        AccountType accountType = accountQueryService.getAccountType(accountId);

        EmissionTradingScheme emissionTradingScheme = accountQueryService.getAccountEmissionTradingScheme(accountId);

        Set<RequestType> availableCreateRequestTypesForAccount = RequestType.getAvailableForAccountCreateRequestTypes(accountType,emissionTradingScheme);

        Set<RequestCreateActionType> actions = getAvailableCreateActions(accountId, appUser, availableCreateRequestTypesForAccount);

        return actions.stream()
                .collect(Collectors.toMap(
                        requestType -> requestType,
                        requestType -> getAccountValidationResult(requestType, accountId)))
                .entrySet().stream()
                .filter(a -> a.getValue().isAvailable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Transactional
    public Map<RequestCreateActionType, RequestCreateValidationResult> getAvailableAerWorkflows(
            final String requestId, final AppUser appUser) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD)
                .requestId(requestId)
                .build();

        AccountType accountType = accountQueryService.getAccountType(request.getAccountId());
        Set<RequestType> availableCreateRequestTypes = RequestType.getAvailableForAERCreateRequestTypes(accountType,
                accountQueryService.getAccountEmissionTradingScheme(request.getAccountId()));
                Set < RequestCreateActionType > actions = this.getAvailableCreateActions(
                        request.getAccountId(), appUser, availableCreateRequestTypes);

        return actions.stream()
                .collect(Collectors.toMap(
                        requestType -> requestType,
                        requestType -> getRequestRelatedValidationResult(requestType, request.getAccountId(), payload)))
                .entrySet().stream()
                .filter(a -> a.getValue().isAvailable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Set<RequestCreateActionType> getAvailableCreateActions(final Long accountId,
                                                                   final AppUser appUser,
                                                                   final Set<RequestType> availableCreateRequestTypes) {
        return accountRequestAuthorizationResourceService
                .findRequestCreateActionsByAccountId(appUser, accountId).stream()
                .map(RequestCreateActionType::valueOf)
                .filter(type -> enabledWorkflowValidator.isWorkflowEnabled(type.getType()))
                .filter(type -> availableCreateRequestTypes.contains(type.getType()))
                .collect(Collectors.toSet());
    }

    private RequestCreateValidationResult getAccountValidationResult(RequestCreateActionType type, long accountId) {
        return requestCreateByAccountValidators.stream()
                .filter(validator -> validator.getType().equals(type))
                .findFirst()
                .map(validator -> validator.validateAction(accountId))
                .orElse(RequestCreateValidationResult.builder().valid(true).build());
    }

    private RequestCreateValidationResult getRequestRelatedValidationResult(RequestCreateActionType type, long accountId, RequestCreateActionPayload payload) {
        return requestCreateByRequestValidators.stream()
                .filter(validator -> validator.getType().equals(type))
                .findFirst()
                .map(validator -> validator.validateAction(accountId, payload))
                .orElse(RequestCreateValidationResult.builder().valid(true).build());
    }
}
