package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAcceptedRequestActionPayload;

import java.util.Set;

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
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
