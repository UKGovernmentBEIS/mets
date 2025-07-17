package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.validation.EnabledWorkflowValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestCreateActionResourceTypeDelegator<T extends RequestCreateActionPayload> {
    private final List<RequestCreateActionResourceTypeHandler<T>> resourceTypeHandlers;
    private final EnabledWorkflowValidator enabledWorkflowValidator;

    public RequestCreateActionResourceTypeHandler<T> getResourceTypeHandler(RequestCreateActionType requestCreateActionType) {
        return resourceTypeHandlers.stream()
                .filter(handler -> enabledWorkflowValidator.isWorkflowEnabled(requestCreateActionType.getType()))
                .filter(handler -> handler.getResourceType().equals(requestCreateActionType.getType().getResourceType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestCreateActionType.getType().getResourceType()));
    }
}
