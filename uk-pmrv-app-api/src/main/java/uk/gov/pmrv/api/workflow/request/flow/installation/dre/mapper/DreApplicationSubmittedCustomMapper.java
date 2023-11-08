package uk.gov.pmrv.api.workflow.request.flow.installation.dre.mapper;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmittedRequestActionPayload;

@Service
public class DreApplicationSubmittedCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private final DreMapper dreMapper = Mappers.getMapper(DreMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        final DreApplicationSubmittedRequestActionPayload entityPayload =
                (DreApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final DreApplicationSubmittedRequestActionPayload dtoPayload = dreMapper
                .cloneSubmittedRequestActionPayloadIgnoreDeterminationReason(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.DRE_APPLICATION_SUBMITTED;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
