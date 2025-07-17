package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.dto.RequestActionAuthorityInfoDTO;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.RequestActionAuthorityInfoProvider;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;

@Service
@RequiredArgsConstructor
public class RequestActionAuthorityInfoQueryService implements RequestActionAuthorityInfoProvider {

    private final RequestActionRepository requestActionRepository;

    @Override
    @Transactional(readOnly = true)
    public RequestActionAuthorityInfoDTO getRequestActionAuthorityInfo(Long requestActionId) {
        return requestActionRepository.findById(requestActionId)
                .map(requestAction -> RequestActionAuthorityInfoDTO.builder()
                        .id(requestAction.getId())
                        .type(requestAction.getType().name())
                        .authorityInfo(ResourceAuthorityInfo.builder()
                                .requestResources(requestAction.getRequest().getRequestResourcesMap())
                                .build())
                        .build())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
