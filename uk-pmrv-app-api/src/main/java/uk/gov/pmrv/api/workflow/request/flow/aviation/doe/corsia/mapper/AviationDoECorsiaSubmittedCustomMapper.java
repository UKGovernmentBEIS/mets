package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmittedRequestActionPayload;

import java.util.Set;

@Service
public class AviationDoECorsiaSubmittedCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private final AviationDoErCorsiaSubmitRequestActionMapper aviationDoECorsiaSubmitRequestActionMapper = Mappers.getMapper(AviationDoErCorsiaSubmitRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        AviationDoECorsiaSubmittedRequestActionPayload entityPayload =
                (AviationDoECorsiaSubmittedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        AviationDoECorsiaSubmittedRequestActionPayload dtoPayload =
                aviationDoECorsiaSubmitRequestActionMapper.cloneCompletedPayloadIgnoreFurtherDetailsComments(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.AVIATION_DOE_CORSIA_SUBMITTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
