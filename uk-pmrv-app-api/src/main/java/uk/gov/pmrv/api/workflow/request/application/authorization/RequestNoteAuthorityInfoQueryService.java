package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.RequestNoteAuthorityInfoProvider;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestNoteRepository;

@RequiredArgsConstructor
@Service
public class RequestNoteAuthorityInfoQueryService implements RequestNoteAuthorityInfoProvider {

    private final RequestNoteRepository requestNoteRepository;

    @Override
    public RequestAuthorityInfoDTO getRequestNoteInfo(final Long id) {

        final Request request = requestNoteRepository.getRequestByNoteId(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return RequestAuthorityInfoDTO.builder()
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .requestResources(request.getRequestResourcesMap())
                        .build())
                .build();
    }
}
