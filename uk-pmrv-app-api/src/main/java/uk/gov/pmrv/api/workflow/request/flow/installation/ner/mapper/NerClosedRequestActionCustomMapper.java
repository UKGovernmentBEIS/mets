package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationEndedRequestActionPayload;

import java.util.Set;

@Service
public class NerClosedRequestActionCustomMapper implements RequestActionCustomMapper {
    
    private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);
    private static final NerMapper NER_MAPPER = Mappers.getMapper(NerMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {

        final NerApplicationEndedRequestActionPayload entityPayload =
            (NerApplicationEndedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);

        final NerApplicationEndedRequestActionPayload dtoPayload =
            NER_MAPPER.toNerApplicationEndedIgnorePaymentComments(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
    
    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.NER_APPLICATION_CLOSED;
    }
}
