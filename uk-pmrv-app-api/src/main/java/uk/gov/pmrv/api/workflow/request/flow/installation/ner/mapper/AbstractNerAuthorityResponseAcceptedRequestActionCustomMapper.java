package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAcceptedRequestActionPayload;

@Service
public abstract class AbstractNerAuthorityResponseAcceptedRequestActionCustomMapper implements RequestActionCustomMapper {

    private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);
    private static final NerMapper NER_MAPPER = Mappers.getMapper(NerMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {

        final NerApplicationAcceptedRequestActionPayload entityPayload =
            (NerApplicationAcceptedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);

        final NerApplicationAcceptedRequestActionPayload dtoPayload =
            NER_MAPPER.toNerApplicationAcceptedIgnoreComments(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
