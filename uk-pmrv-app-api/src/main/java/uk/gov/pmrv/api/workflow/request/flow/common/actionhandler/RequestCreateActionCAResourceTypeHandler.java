package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.competentauthority.CompetentAuthorityService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByCAValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestCreateActionCAResourceTypeHandler<T extends RequestCreateActionPayload> implements RequestCreateActionResourceTypeHandler<T> {
    private final List<RequestCreateByCAValidator<T>> requestCreateByCAValidators;
    private final List<RequestCACreateActionHandler<T>> requestCACreateActionHandlers;
    private final CompetentAuthorityService competentAuthorityService;

    @Override
    public String process(String resourceId, RequestCreateActionType requestCreateActionType, T payload, AppUser appUser) {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.valueOf(resourceId);

        competentAuthorityService.exclusiveLockCompetentAuthority(ca);

        final RequestCreateValidationResult validationResult = requestCreateByCAValidators
                .stream()
                .filter(requestCreateValidator -> requestCreateValidator.getType().equals(requestCreateActionType))
                .findFirst()
                .map(requestCreateBycaValidator -> requestCreateBycaValidator.validateAction(ca, payload))
                .orElse(RequestCreateValidationResult.builder().valid(true).isAvailable(true).build());

        if (!validationResult.isValid() || !validationResult.isAvailable()) {
            throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, validationResult);
        }

        return requestCACreateActionHandlers.stream()
                .filter(handler -> handler.getRequestCreateActionType().equals(requestCreateActionType))
                .findFirst()
                .map(handler -> handler.process(ca, payload, appUser))
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestCreateActionType));
    }

    @Override
    public String getResourceType() {
        return ResourceType.CA;
    }
}
