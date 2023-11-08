package uk.gov.pmrv.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestTaskService {

    private final RequestTaskRepository requestTaskRepository;
    private final AuthorizationRulesQueryService authorizationRulesQueryService;

    public RequestTask findTaskById(Long id) {
        return requestTaskRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public void updateRequestTaskPayload(RequestTask requestTask, RequestTaskPayload requestTaskPayload) {
        requestTask.setPayload(requestTaskPayload);
    }

    public List<RequestTask> findTasksByRequestIdAndRoleType(String requestId, RoleType roleType) {
        Set<String> roleAllowedTaskTypes = authorizationRulesQueryService
            .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, roleType);

        return requestTaskRepository.findByRequestId(requestId).stream()
            .filter(requestTask -> roleAllowedTaskTypes.contains(requestTask.getType().name()))
            .collect(Collectors.toList());
    }

    public List<RequestTask> findTasksByTypeInAndAccountId(Set<RequestTaskType> types, Long accountId) {
        return requestTaskRepository.findByTypeInAndRequestAccountId(types, accountId);
    }
}
