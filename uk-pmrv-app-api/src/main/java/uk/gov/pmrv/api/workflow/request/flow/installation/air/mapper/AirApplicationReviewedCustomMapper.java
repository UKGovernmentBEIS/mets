package uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewedRequestActionPayload;

import java.util.Set;

@Service
public class AirApplicationReviewedCustomMapper implements RequestActionCustomMapper {

    private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);
    private static final AirMapper AIR_MAPPER = Mappers.getMapper(AirMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        
        final AirApplicationReviewedRequestActionPayload entityPayload =
                (AirApplicationReviewedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);
        final AirApplicationReviewedRequestActionPayload dtoPayload = AIR_MAPPER.cloneReviewedPayloadIgnoreComments(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.AIR_APPLICATION_REVIEWED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
